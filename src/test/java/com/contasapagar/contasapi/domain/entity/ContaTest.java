package com.contasapagar.contasapi.domain.entity;

import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade Conta
 */
@DisplayName("Conta Entity Tests")
class ContaTest {

    private LocalDate dataVencimento;
    private BigDecimal valor;
    private String descricao;

    @BeforeEach
    void setUp() {
        dataVencimento = LocalDate.of(2024, 12, 31);
        valor = BigDecimal.valueOf(150.50);
        descricao = "Conta de luz";
    }

    @Nested
    @DisplayName("Criação de Conta")
    class CriacaoContaTest {

        @Test
        @DisplayName("Deve criar conta com dados válidos")
        void devecriarContaComDadosValidos() {
            // Act
            Conta conta = new Conta(dataVencimento, valor, descricao);

            // Assert
            assertNotNull(conta);
            assertEquals(dataVencimento, conta.getDataVencimento());
            assertEquals(valor, conta.getValor());
            assertEquals(descricao, conta.getDescricao());
            assertEquals(SituacaoEnum.PENDENTE, conta.getSituacao());
            assertNull(conta.getDataPagamento());
            assertNotNull(conta.getCreatedAt());
            assertNotNull(conta.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve falhar com data de vencimento nula")
        void deveFalharComDataVencimentoNula() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                new Conta(null, valor, descricao);
            });
        }

        @Test
        @DisplayName("Deve falhar com valor nulo")
        void deveFalharComValorNulo() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                new Conta(dataVencimento, null, descricao);
            });
        }

        @Test
        @DisplayName("Deve falhar com valor zero ou negativo")
        void deveFalharComValorZeroOuNegativo() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                new Conta(dataVencimento, BigDecimal.ZERO, descricao);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Conta(dataVencimento, BigDecimal.valueOf(-10), descricao);
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição nula")
        void deveFalharComDescricaoNula() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                new Conta(dataVencimento, valor, null);
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição vazia")
        void deveFalharComDescricaoVazia() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                new Conta(dataVencimento, valor, "");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Conta(dataVencimento, valor, "   ");
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição muito longa")
        void deveFalharComDescricaoMuitoLonga() {
            // Arrange
            String descricaoLonga = "a".repeat(501);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                new Conta(dataVencimento, valor, descricaoLonga);
            });
        }
    }

    @Nested
    @DisplayName("Marcar como Pago")
    class MarcarComoPagoTest {

        private Conta conta;

        @BeforeEach
        void setUp() {
            conta = new Conta(dataVencimento, valor, descricao);
        }

        @Test
        @DisplayName("Deve marcar conta pendente como pago")
        void deveMarcarContaPendenteComoPago() {
            // Arrange
            LocalDate dataPagamento = LocalDate.of(2024, 12, 15);

            // Act
            conta.marcarComoPago(dataPagamento);

            // Assert
            assertEquals(SituacaoEnum.PAGO, conta.getSituacao());
            assertEquals(dataPagamento, conta.getDataPagamento());
        }

        @Test
        @DisplayName("Deve marcar conta vencida como pago")
        void deveMarcarContaVencidaComoPago() {
            // Arrange
            conta = new Conta(LocalDate.of(2024, 1, 1), valor, descricao);
            conta.verificarVencimento(); // Força situação VENCIDO
            LocalDate dataPagamento = LocalDate.of(2024, 12, 15);

            // Act
            conta.marcarComoPago(dataPagamento);

            // Assert
            assertEquals(SituacaoEnum.PAGO, conta.getSituacao());
            assertEquals(dataPagamento, conta.getDataPagamento());
        }

        @Test
        @DisplayName("Deve falhar ao marcar conta já paga")
        void deveFalharAoMarcarContaJaPaga() {
            // Arrange
            conta.marcarComoPago(LocalDate.of(2024, 12, 10));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.marcarComoPago(LocalDate.of(2024, 12, 15));
            });
        }

        @Test
        @DisplayName("Deve falhar ao marcar conta cancelada")
        void deveFalharAoMarcarContaCancelada() {
            // Arrange
            conta.cancelar();

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.marcarComoPago(LocalDate.of(2024, 12, 15));
            });
        }

        @Test
        @DisplayName("Deve falhar com data de pagamento nula")
        void deveFalharComDataPagamentoNula() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                conta.marcarComoPago(null);
            });
        }
    }

    @Nested
    @DisplayName("Cancelar Conta")
    class CancelarContaTest {

        private Conta conta;

        @BeforeEach
        void setUp() {
            conta = new Conta(dataVencimento, valor, descricao);
        }

        @Test
        @DisplayName("Deve cancelar conta pendente")
        void deveCancelarContaPendente() {
            // Act
            conta.cancelar();

            // Assert
            assertEquals(SituacaoEnum.CANCELADO, conta.getSituacao());
            assertNull(conta.getDataPagamento());
        }

        @Test
        @DisplayName("Deve cancelar conta vencida")
        void deveCancelarContaVencida() {
            // Arrange
            conta = new Conta(LocalDate.of(2024, 1, 1), valor, descricao);
            conta.verificarVencimento(); // Força situação VENCIDO

            // Act
            conta.cancelar();

            // Assert
            assertEquals(SituacaoEnum.CANCELADO, conta.getSituacao());
        }

        @Test
        @DisplayName("Deve falhar ao cancelar conta já paga")
        void deveFalharAoCancelarContaJaPaga() {
            // Arrange
            conta.marcarComoPago(LocalDate.of(2024, 12, 15));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.cancelar();
            });
        }

        @Test
        @DisplayName("Deve falhar ao cancelar conta já cancelada")
        void deveFalharAoCancelarContaJaCancelada() {
            // Arrange
            conta.cancelar();

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.cancelar();
            });
        }
    }

    @Nested
    @DisplayName("Verificar Vencimento")
    class VerificarVencimentoTest {

        @Test
        @DisplayName("Deve marcar como vencida se data passou")
        void deveMarcarComoVencidaSeDataPassou() {
            // Arrange
            LocalDate dataPassada = LocalDate.now().minusDays(1);
            Conta conta = new Conta(dataPassada, valor, descricao);

            // Act
            conta.verificarVencimento();

            // Assert
            assertEquals(SituacaoEnum.VENCIDO, conta.getSituacao());
        }

        @Test
        @DisplayName("Deve manter pendente se ainda não venceu")
        void deveManterPendenteSeAindaNaoVenceu() {
            // Arrange
            LocalDate dataFutura = LocalDate.now().plusDays(1);
            Conta conta = new Conta(dataFutura, valor, descricao);

            // Act
            conta.verificarVencimento();

            // Assert
            assertEquals(SituacaoEnum.PENDENTE, conta.getSituacao());
        }

        @Test
        @DisplayName("Não deve alterar conta já paga")
        void naoDeveAlterarContaJaPaga() {
            // Arrange
            LocalDate dataPassada = LocalDate.now().minusDays(1);
            Conta conta = new Conta(dataPassada, valor, descricao);
            conta.marcarComoPago(LocalDate.now());

            // Act
            conta.verificarVencimento();

            // Assert
            assertEquals(SituacaoEnum.PAGO, conta.getSituacao());
        }
    }

    @Nested
    @DisplayName("Atualizar Conta")
    class AtualizarContaTest {

        private Conta conta;

        @BeforeEach
        void setUp() {
            conta = new Conta(dataVencimento, valor, descricao);
        }

        @Test
        @DisplayName("Deve atualizar dados da conta pendente")
        void deveAtualizarDadosContaPendente() {
            // Arrange
            LocalDate novaDataVencimento = LocalDate.of(2025, 1, 15);
            BigDecimal novoValor = BigDecimal.valueOf(200.00);
            String novaDescricao = "Conta de água";

            // Act
            conta.atualizar(novaDataVencimento, novoValor, novaDescricao);

            // Assert
            assertEquals(novaDataVencimento, conta.getDataVencimento());
            assertEquals(novoValor, conta.getValor());
            assertEquals(novaDescricao, conta.getDescricao());
        }

        @Test
        @DisplayName("Deve falhar ao atualizar conta paga")
        void deveFalharAoAtualizarContaPaga() {
            // Arrange
            conta.marcarComoPago(LocalDate.now());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.atualizar(LocalDate.of(2025, 1, 15), BigDecimal.valueOf(200.00), "Nova descrição");
            });
        }

        @Test
        @DisplayName("Deve falhar ao atualizar conta cancelada")
        void deveFalharAoAtualizarContaCancelada() {
            // Arrange
            conta.cancelar();

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> {
                conta.atualizar(LocalDate.of(2025, 1, 15), BigDecimal.valueOf(200.00), "Nova descrição");
            });
        }

        @Test
        @DisplayName("Deve reverter de vencida para pendente se data futura")
        void deveReverterDeVencidaParaPendenteSeDataFutura() {
            // Arrange
            conta = new Conta(LocalDate.now().minusDays(1), valor, descricao);
            conta.verificarVencimento(); // Força VENCIDO
            LocalDate dataFutura = LocalDate.now().plusDays(10);

            // Act
            conta.atualizar(dataFutura, valor, descricao);

            // Assert
            assertEquals(SituacaoEnum.PENDENTE, conta.getSituacao());
        }
    }

    @Nested
    @DisplayName("Verificações de Estado")
    class VerificacoesEstadoTest {

        @Test
        @DisplayName("Deve identificar conta vencida corretamente")
        void deveIdentificarContaVencidaCorretamente() {
            // Arrange
            LocalDate dataPassada = LocalDate.now().minusDays(1);
            Conta contaVencida = new Conta(dataPassada, valor, descricao);

            LocalDate dataFutura = LocalDate.now().plusDays(1);
            Conta contaNaoVencida = new Conta(dataFutura, valor, descricao);

            Conta contaPaga = new Conta(dataPassada, valor, descricao);
            contaPaga.marcarComoPago(LocalDate.now());

            // Assert
            assertTrue(contaVencida.isVencida());
            assertFalse(contaNaoVencida.isVencida());
            assertFalse(contaPaga.isVencida()); // Conta paga não é considerada vencida
        }
    }
}
