package com.contasapagar.contasapi.infrastructure.repository;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para ContaJpaRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ContaJpaRepository Integration Tests")
class ContaJpaRepositoryTest {

    @Autowired
    private ContaJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Conta contaPendente;
    private Conta contaPaga;
    private Conta contaVencida;
    private Conta contaCancelada;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Criar dados de teste
        contaPendente = new Conta(
                LocalDate.of(2024, 12, 31),
                BigDecimal.valueOf(150.50),
                "Conta de luz pendente"
        );

        contaPaga = new Conta(
                LocalDate.of(2024, 11, 30),
                BigDecimal.valueOf(89.90),
                "Conta de água paga"
        );
        contaPaga.marcarComoPago(LocalDate.of(2024, 12, 1));

        contaVencida = new Conta(
                LocalDate.of(2024, 1, 15),
                BigDecimal.valueOf(200.00),
                "Conta de internet vencida"
        );
        contaVencida.verificarVencimento(); // Força situação VENCIDO

        contaCancelada = new Conta(
                LocalDate.of(2024, 6, 15),
                BigDecimal.valueOf(75.00),
                "Conta de telefone cancelada"
        );
        contaCancelada.cancelar();

        // Persistir no banco
        repository.saveAll(List.of(contaPendente, contaPaga, contaVencida, contaCancelada));
        entityManager.flush();
    }

    @Nested
    @DisplayName("Busca por Situação")
    class BuscaPorSituacaoTest {

        @Test
        @DisplayName("Deve buscar contas por situação PENDENTE")
        void deveBuscarContasPorSituacaoPendente() {
            // Act
            List<Conta> contas = repository.findBySituacao(SituacaoEnum.PENDENTE);

            // Assert
            assertEquals(1, contas.size());
            assertEquals("Conta de luz pendente", contas.get(0).getDescricao());
        }

        @Test
        @DisplayName("Deve buscar contas por situação PAGO")
        void deveBuscarContasPorSituacaoPago() {
            // Act
            List<Conta> contas = repository.findBySituacao(SituacaoEnum.PAGO);

            // Assert
            assertEquals(1, contas.size());
            assertEquals("Conta de água paga", contas.get(0).getDescricao());
        }

        @Test
        @DisplayName("Deve buscar contas por situação VENCIDO")
        void deveBuscarContasPorSituacaoVencido() {
            // Act
            List<Conta> contas = repository.findBySituacao(SituacaoEnum.VENCIDO);

            // Assert
            assertEquals(1, contas.size());
            assertEquals("Conta de internet vencida", contas.get(0).getDescricao());
        }

        @Test
        @DisplayName("Deve buscar contas por situação CANCELADO")
        void deveBuscarContasPorSituacaoCancelado() {
            // Act
            List<Conta> contas = repository.findBySituacao(SituacaoEnum.CANCELADO);

            // Assert
            assertEquals(1, contas.size());
            assertEquals("Conta de telefone cancelada", contas.get(0).getDescricao());
        }
    }

    @Nested
    @DisplayName("Busca por Data de Vencimento")
    class BuscaPorDataVencimentoTest {

        @Test
        @DisplayName("Deve buscar contas por data de vencimento específica")
        void deveBuscarContasPorDataVencimentoEspecifica() {
            // Act
            List<Conta> contas = repository.findByDataVencimento(LocalDate.of(2024, 12, 31));

            // Assert
            assertEquals(1, contas.size());
            assertEquals("Conta de luz pendente", contas.get(0).getDescricao());
        }

        @Test
        @DisplayName("Deve buscar contas por período de vencimento")
        void deveBuscarContasPorPeriodoVencimento() {
            // Act
            List<Conta> contas = repository.findByPeriodoVencimento(
                    LocalDate.of(2024, 11, 1),
                    LocalDate.of(2024, 12, 31)
            );

            // Assert
            assertEquals(2, contas.size());
            assertTrue(contas.stream().anyMatch(c -> c.getDescricao().contains("luz")));
            assertTrue(contas.stream().anyMatch(c -> c.getDescricao().contains("água")));
        }
    }

    @Nested
    @DisplayName("Busca de Contas Vencidas")
    class BuscaContasVencidasTest {

        @Test
        @DisplayName("Deve buscar contas vencidas")
        void deveBuscarContasVencidas() {
            // Act
            List<Conta> contasVencidas = repository.findContasVencidas();

            // Assert
            assertFalse(contasVencidas.isEmpty());
            assertTrue(contasVencidas.stream()
                    .allMatch(conta -> conta.getSituacao() == SituacaoEnum.PENDENTE &&
                            conta.getDataVencimento().isBefore(LocalDate.now())));
        }
    }

    @Nested
    @DisplayName("Filtros Dinâmicos")
    class FiltrosDinamicosTest {

        @Test
        @DisplayName("Deve filtrar por descrição")
        void deveFiltrarPorDescricao() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Conta> resultado = repository.findByFiltros(
                    null, null, "luz", null, pageable
            );

            // Assert
            assertEquals(1, resultado.getTotalElements());
            assertTrue(resultado.getContent().get(0).getDescricao().contains("luz"));
        }

        @Test
        @DisplayName("Deve filtrar por situação")
        void deveFiltrarPorSituacao() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Conta> resultado = repository.findByFiltros(
                    null, null, null, "PAGO", pageable
            );

            // Assert
            assertEquals(1, resultado.getTotalElements());
            assertEquals(SituacaoEnum.PAGO, resultado.getContent().get(0).getSituacao());
        }

        @Test
        @DisplayName("Deve filtrar por período de vencimento")
        void deveFiltrarPorPeriodoVencimento() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Conta> resultado = repository.findByFiltros(
                    LocalDate.of(2024, 12, 1),
                    LocalDate.of(2024, 12, 31),
                    null, null, pageable
            );

            // Assert
            assertEquals(1, resultado.getTotalElements());
            assertTrue(resultado.getContent().get(0).getDataVencimento()
                    .isAfter(LocalDate.of(2024, 11, 30)));
        }

        @Test
        @DisplayName("Deve combinar múltiplos filtros")
        void deveCombinarMultiplosFiltros() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Conta> resultado = repository.findByFiltros(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31),
                    "pendente",
                    "PENDENTE",
                    pageable
            );

            // Assert
            assertEquals(1, resultado.getTotalElements());
            Conta conta = resultado.getContent().get(0);
            assertEquals(SituacaoEnum.PENDENTE, conta.getSituacao());
            assertTrue(conta.getDescricao().toLowerCase().contains("pendente"));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há resultados")
        void deveRetornarPaginaVaziaQuandoNaoHaResultados() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Conta> resultado = repository.findByFiltros(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    null, null, pageable
            );

            // Assert
            assertEquals(0, resultado.getTotalElements());
            assertTrue(resultado.getContent().isEmpty());
        }
    }

    @Nested
    @DisplayName("Cálculos e Contagens")
    class CalculosContagensTest {

        @Test
        @DisplayName("Deve calcular total pago por período")
        void deveCalcularTotalPagoPorPeriodo() {
            // Act
            BigDecimal total = repository.calcularTotalPagoPorPeriodo(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31)
            );

            // Assert
            assertNotNull(total);
            assertEquals(0, total.compareTo(BigDecimal.valueOf(89.90)));
        }

        @Test
        @DisplayName("Deve contar contas pagas por período")
        void deveContarContasPagasPorPeriodo() {
            // Act
            Integer count = repository.contarContasPagasPorPeriodo(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31)
            );

            // Assert
            assertEquals(1, count);
        }

        @Test
        @DisplayName("Deve contar por situação")
        void deveContarPorSituacao() {
            // Act
            long countPendente = repository.countBySituacao(SituacaoEnum.PENDENTE);
            long countPago = repository.countBySituacao(SituacaoEnum.PAGO);
            long countVencido = repository.countBySituacao(SituacaoEnum.VENCIDO);
            long countCancelado = repository.countBySituacao(SituacaoEnum.CANCELADO);

            // Assert
            assertEquals(1, countPendente);
            assertEquals(1, countPago);
            assertEquals(1, countVencido);
            assertEquals(1, countCancelado);
        }

        @Test
        @DisplayName("Deve retornar zero para período sem contas pagas")
        void deveRetornarZeroParaPeriodoSemContasPagas() {
            // Act
            BigDecimal total = repository.calcularTotalPagoPorPeriodo(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31)
            );

            // Assert
            assertEquals(0, total.compareTo(BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Operações CRUD")
    class OperacoesCrudTest {

        @Test
        @DisplayName("Deve salvar nova conta")
        void deveSalvarNovaConta() {
            // Arrange
            Conta novaConta = new Conta(
                    LocalDate.of(2024, 12, 15),
                    BigDecimal.valueOf(99.99),
                    "Nova conta de teste"
            );

            // Act
            Conta contaSalva = repository.save(novaConta);

            // Assert
            assertNotNull(contaSalva.getId());
            assertEquals("Nova conta de teste", contaSalva.getDescricao());
            assertTrue(repository.existsById(contaSalva.getId()));
        }

        @Test
        @DisplayName("Deve atualizar conta existente")
        void deveAtualizarContaExistente() {
            // Arrange
            contaPendente.atualizar(
                    LocalDate.of(2025, 1, 15),
                    BigDecimal.valueOf(175.00),
                    "Conta de luz atualizada"
            );

            // Act
            Conta contaAtualizada = repository.save(contaPendente);

            // Assert
            assertEquals("Conta de luz atualizada", contaAtualizada.getDescricao());
            assertEquals(0, contaAtualizada.getValor().compareTo(BigDecimal.valueOf(175.00)));
        }

        @Test
        @DisplayName("Deve deletar conta")
        void deveDeletarConta() {
            // Arrange
            Long id = contaPendente.getId();

            // Act
            repository.deleteById(id);

            // Assert
            assertFalse(repository.existsById(id));
        }

        @Test
        @DisplayName("Deve contar total de registros")
        void deveContarTotalRegistros() {
            // Act
            long total = repository.count();

            // Assert
            assertEquals(4, total);
        }
    }
}
