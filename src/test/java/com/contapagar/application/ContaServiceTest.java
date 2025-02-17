package com.contapagar.application;

import com.contapagar.domain.exception.RegistroNaoEncontradoException;
import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import com.contapagar.domain.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setId(1L);
        conta.setDataVencimento(LocalDate.now().plusDays(10));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Conta de Teste");
        conta.setSituacao(SituacaoConta.PENDENTE);
    }

    @Test
    void deveCriarConta() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta resultado = contaService.criar(conta);

        assertNotNull(resultado);
        assertEquals("Conta de Teste", resultado.getDescricao());
    }

    @Test
    void deveObterContaPorId() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        Conta resultado = contaService.obterPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoForEncontrada() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RegistroNaoEncontradoException.class, () -> contaService.obterPorId(1L));
    }

    @Test
    void deveListarContas() {
        Page<Conta> page = new PageImpl<>(List.of(conta));
        when(contaRepository.filtrar(any(), any(), any())).thenReturn(page);

        Page<Conta> resultado = contaService.listarComFiltros(null, null, Pageable.unpaged());

        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveCalcularTotalPago() {
        when(contaRepository.calcularTotalPago(any(), any())).thenReturn(BigDecimal.valueOf(300));

        BigDecimal total = contaService.calcularTotalPagoPeriodo(LocalDate.now().minusDays(30), LocalDate.now());

        assertEquals(BigDecimal.valueOf(300), total);
    }
}
