package com.contasapagar.contasapi.application.service;

import com.contasapagar.contasapi.application.dto.*;
import com.contasapagar.contasapi.application.mapper.ContaMapper;
import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ContaService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContaService Tests")
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ContaMapper contaMapper;

    @InjectMocks
    private ContaService contaService;

    private ContaRequestDto contaRequestDto;
    private Conta conta;
    private ContaResponseDto contaResponseDto;

    @BeforeEach
    void setUp() {
        contaRequestDto = new ContaRequestDto(
                LocalDate.of(2024, 12, 31),
                BigDecimal.valueOf(150.50),
                "Conta de luz"
        );

        conta = new Conta(
                LocalDate.of(2024, 12, 31),
                BigDecimal.valueOf(150.50),
                "Conta de luz"
        );

        contaResponseDto = new ContaResponseDto(
                1L,
                LocalDate.of(2024, 12, 31),
                null,
                BigDecimal.valueOf(150.50),
                "Conta de luz",
                SituacaoEnum.PENDENTE,
                conta.getCreatedAt(),
                conta.getUpdatedAt()
        );
    }

    @Nested
    @DisplayName("Cadastrar Conta")
    class CadastrarContaTest {

        @Test
        @DisplayName("Deve cadastrar conta com sucesso")
        void deveCadastrarContaComSucesso() {
            // Arrange
            when(contaMapper.toEntity(contaRequestDto)).thenReturn(conta);
            when(contaRepository.save(conta)).thenReturn(conta);
            when(contaMapper.toDto(conta)).thenReturn(contaResponseDto);

            // Act
            ContaResponseDto resultado = contaService.cadastrar(contaRequestDto);

            // Assert
            assertNotNull(resultado);
            assertEquals(contaResponseDto.getId(), resultado.getId());
            verify(contaMapper).toEntity(contaRequestDto);
            verify(contaRepository).save(conta);
            verify(contaMapper).toDto(conta);
        }

        @Test
        @DisplayName("Deve propagar exceção do repositório")
        void devePropagarExcecaoDoRepositorio() {
            // Arrange
            when(contaMapper.toEntity(contaRequestDto)).thenReturn(conta);
            when(contaRepository.save(conta)).thenThrow(new RuntimeException("Erro no banco"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                contaService.cadastrar(contaRequestDto);
            });

            assertTrue(exception.getMessage().contains("Erro ao cadastrar conta"));
        }
    }

    @Nested
    @DisplayName("Atualizar Conta")
    class AtualizarContaTest {

        @Test
        @DisplayName("Deve atualizar conta existente")
        void deveAtualizarContaExistente() {
            // Arrange
            Long id = 1L;
            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
            when(contaRepository.save(conta)).thenReturn(conta);
            when(contaMapper.toDto(conta)).thenReturn(contaResponseDto);

            // Act
            ContaResponseDto resultado = contaService.atualizar(id, contaRequestDto);

            // Assert
            assertNotNull(resultado);
            verify(contaRepository).findById(id);
            verify(contaMapper).updateEntity(conta, contaRequestDto);
            verify(contaRepository).save(conta);
        }

        @Test
        @DisplayName("Deve lançar exceção para conta não encontrada")
        void deveLancarExcecaoParaContaNaoEncontrada() {
            // Arrange
            Long id = 999L;
            when(contaRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ContaNaoEncontradaException.class, () -> {
                contaService.atualizar(id, contaRequestDto);
            });
        }
    }

    @Nested
    @DisplayName("Alterar Situação")
    class AlterarSituacaoTest {

        @Test
        @DisplayName("Deve alterar situação para PAGO")
        void deveAlterarSituacaoParaPago() {
            // Arrange
            Long id = 1L;
            LocalDate dataPagamento = LocalDate.of(2024, 12, 15);
            AlterarSituacaoDto alterarSituacaoDto = new AlterarSituacaoDto(SituacaoEnum.PAGO, dataPagamento);

            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
            when(contaRepository.save(conta)).thenReturn(conta);
            when(contaMapper.toDto(conta)).thenReturn(contaResponseDto);

            // Act
            ContaResponseDto resultado = contaService.alterarSituacao(id, alterarSituacaoDto);

            // Assert
            assertNotNull(resultado);
            verify(contaRepository).findById(id);
            verify(contaRepository).save(conta);
        }

        @Test
        @DisplayName("Deve alterar situação para CANCELADO")
        void deveAlterarSituacaoParaCancelado() {
            // Arrange
            Long id = 1L;
            AlterarSituacaoDto alterarSituacaoDto = new AlterarSituacaoDto(SituacaoEnum.CANCELADO, null);

            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
            when(contaRepository.save(conta)).thenReturn(conta);
            when(contaMapper.toDto(conta)).thenReturn(contaResponseDto);

            // Act
            ContaResponseDto resultado = contaService.alterarSituacao(id, alterarSituacaoDto);

            // Assert
            assertNotNull(resultado);
            verify(contaRepository).findById(id);
            verify(contaRepository).save(conta);
        }

        @Test
        @DisplayName("Deve falhar ao tentar alterar para PENDENTE")
        void deveFalharAoTentarAlterarParaPendente() {
            // Arrange
            Long id = 1L;
            AlterarSituacaoDto alterarSituacaoDto = new AlterarSituacaoDto(SituacaoEnum.PENDENTE, null);

            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                contaService.alterarSituacao(id, alterarSituacaoDto);
            });
        }
    }

    @Nested
    @DisplayName("Buscar Por ID")
    class BuscarPorIdTest {

        @Test
        @DisplayName("Deve buscar conta existente por ID")
        void deveBuscarContaExistentePorId() {
            // Arrange
            Long id = 1L;
            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
            when(contaMapper.toDto(conta)).thenReturn(contaResponseDto);

            // Act
            ContaResponseDto resultado = contaService.buscarPorId(id);

            // Assert
            assertNotNull(resultado);
            assertEquals(contaResponseDto.getId(), resultado.getId());
            verify(contaRepository).findById(id);
            verify(contaMapper).toDto(conta);
        }

        @Test
        @DisplayName("Deve lançar exceção para ID não encontrado")
        void deveLancarExcecaoParaIdNaoEncontrado() {
            // Arrange
            Long id = 999L;
            when(contaRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ContaNaoEncontradaException.class, () -> {
                contaService.buscarPorId(id);
            });
        }
    }

    @Nested
    @DisplayName("Listar Contas")
    class ListarContasTest {

        @Test
        @DisplayName("Deve listar contas sem filtros")
        void deveListarContasSemFiltros() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            ContaFiltroDto filtro = new ContaFiltroDto();
            Page<Conta> contasPage = new PageImpl<>(List.of(conta), pageable, 1);
            Page<ContaResponseDto> contasResponsePage = new PageImpl<>(List.of(contaResponseDto), pageable, 1);

            when(contaRepository.findAll(pageable)).thenReturn(contasPage);
            when(contaMapper.toDto(contasPage)).thenReturn(contasResponsePage);

            // Act
            Page<ContaResponseDto> resultado = contaService.listar(filtro, pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            verify(contaRepository).findAll(pageable);
            verify(contaMapper).toDto(contasPage);
        }

        @Test
        @DisplayName("Deve listar contas com filtros")
        void deveListarContasComFiltros() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            ContaFiltroDto filtro = new ContaFiltroDto();
            filtro.setDataVencimentoInicio(LocalDate.of(2024, 1, 1));
            filtro.setDataVencimentoFim(LocalDate.of(2024, 12, 31));
            filtro.setDescricao("luz");
            filtro.setSituacao(SituacaoEnum.PENDENTE);

            // Criar uma conta que atende aos filtros (já inicia como PENDENTE por padrão)
            Conta contaFiltrada = new Conta(
                    LocalDate.of(2024, 6, 15), // dentro do período
                    BigDecimal.valueOf(100.00),
                    "Conta de luz - Filtro teste"
            );

            Page<Conta> contasPage = new PageImpl<>(List.of(conta, contaFiltrada), pageable, 2);
            Page<ContaResponseDto> contasResponsePage = new PageImpl<>(List.of(contaResponseDto), pageable, 1);

            // Mock para findAll (usado quando há filtros)
            when(contaRepository.findAll(eq(pageable))).thenReturn(contasPage);
            when(contaMapper.toDto(any(Page.class))).thenReturn(contasResponsePage);

            // Act
            Page<ContaResponseDto> resultado = contaService.listar(filtro, pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            verify(contaRepository).findAll(pageable);
            verify(contaMapper).toDto(any(Page.class));
        }
    }

    @Nested
    @DisplayName("Calcular Valor Total Pago")
    class CalcularValorTotalPagoTest {

        @Test
        @DisplayName("Deve calcular valor total pago por período")
        void deveCalcularValorTotalPagoPorPeriodo() {
            // Arrange
            Periodo periodo = new Periodo(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            BigDecimal valorTotal = BigDecimal.valueOf(500.00);
            Integer quantidadeContas = 3;
            ValorTotalPagoPeriodoDto expectedDto = new ValorTotalPagoPeriodoDto(
                    periodo.getDataInicio(),
                    periodo.getDataFim(),
                    valorTotal,
                    quantidadeContas
            );

            when(contaRepository.calcularValorTotalPagoPorPeriodo(periodo.getDataInicio(), periodo.getDataFim()))
                    .thenReturn(valorTotal);
            when(contaRepository.contarContasPagasPorPeriodo(periodo.getDataInicio(), periodo.getDataFim()))
                    .thenReturn(quantidadeContas);
            when(contaMapper.toValorTotalPagoPeriodoDto(periodo, valorTotal, quantidadeContas))
                    .thenReturn(expectedDto);

            // Act
            ValorTotalPagoPeriodoDto resultado = contaService.calcularValorTotalPagoPorPeriodo(periodo);

            // Assert
            assertNotNull(resultado);
            assertEquals(valorTotal, resultado.getValorTotal());
            assertEquals(quantidadeContas, resultado.getQuantidadeContas());
            verify(contaRepository).calcularValorTotalPagoPorPeriodo(periodo.getDataInicio(), periodo.getDataFim());
            verify(contaRepository).contarContasPagasPorPeriodo(periodo.getDataInicio(), periodo.getDataFim());
            verify(contaMapper).toValorTotalPagoPeriodoDto(periodo, valorTotal, quantidadeContas);
        }

        @Test
        @DisplayName("Deve lidar com valores nulos do repositório")
        void deveLidarComValoresNulosDoRepositorio() {
            // Arrange
            Periodo periodo = new Periodo(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            ValorTotalPagoPeriodoDto expectedDto = new ValorTotalPagoPeriodoDto(
                    periodo.getDataInicio(),
                    periodo.getDataFim(),
                    BigDecimal.ZERO,
                    0
            );

            when(contaRepository.calcularValorTotalPagoPorPeriodo(periodo.getDataInicio(), periodo.getDataFim()))
                    .thenReturn(null);
            when(contaRepository.contarContasPagasPorPeriodo(periodo.getDataInicio(), periodo.getDataFim()))
                    .thenReturn(null);
            when(contaMapper.toValorTotalPagoPeriodoDto(periodo, null, null))
                    .thenReturn(expectedDto);

            // Act
            ValorTotalPagoPeriodoDto resultado = contaService.calcularValorTotalPagoPorPeriodo(periodo);

            // Assert
            assertNotNull(resultado);
            verify(contaMapper).toValorTotalPagoPeriodoDto(periodo, null, null);
        }
    }

    @Nested
    @DisplayName("Remover Conta")
    class RemoverContaTest {

        @Test
        @DisplayName("Deve remover conta existente")
        void deveRemoverContaExistente() {
            // Arrange
            Long id = 1L;
            when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

            // Act
            assertDoesNotThrow(() -> contaService.remover(id));

            // Assert
            verify(contaRepository).findById(id);
            verify(contaRepository).delete(conta);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar remover conta inexistente")
        void deveLancarExcecaoAoTentarRemoverContaInexistente() {
            // Arrange
            Long id = 999L;
            when(contaRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ContaNaoEncontradaException.class, () -> {
                contaService.remover(id);
            });

            verify(contaRepository).findById(id);
            verify(contaRepository, never()).delete(any(Conta.class));
        }
    }
}
