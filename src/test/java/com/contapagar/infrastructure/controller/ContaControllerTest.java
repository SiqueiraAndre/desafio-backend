package com.contapagar.infrastructure.controller;

import com.contapagar.application.ContaService;
import com.contapagar.controller.ContaController;
import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock
    private ContaService contaService;

    @InjectMocks
    private ContaController contaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Conta conta;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contaController).build();
        objectMapper = new ObjectMapper();

        conta = new Conta();
        conta.setId(1L);
        conta.setDataVencimento(LocalDate.now().plusDays(10));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Conta de Teste");
        conta.setSituacao(SituacaoConta.PENDENTE);
    }

    @Test
    void deveCriarConta() throws Exception {
        when(contaService.criar(any())).thenReturn(conta);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Conta de Teste"));
    }

    @Test
    void deveObterContaPorId() throws Exception {
        when(contaService.obterPorId(1L)).thenReturn(conta);

        mockMvc.perform(get("/api/contas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveExcluirConta() throws Exception {
        mockMvc.perform(delete("/api/contas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveObterTotalPago() throws Exception {
        when(contaService.calcularTotalPagoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(500));

        mockMvc.perform(get("/api/contas/total-pago")
                        .param("inicio", "2024-01-01")
                        .param("fim", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500));
    }
}