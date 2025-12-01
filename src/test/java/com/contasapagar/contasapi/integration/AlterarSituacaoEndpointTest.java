package com.contasapagar.contasapi.integration;

import com.contasapagar.contasapi.ContasApiApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ContasApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test-simple")
@Transactional
public class AlterarSituacaoEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void deveAlterarSituacaoComEnumDescricao() throws Exception {
        // Primeiro, criar uma conta
        String contaJson = """
            {
              "dataVencimento": "2025-12-31",
              "valor": 150.50,
              "descricao": "Conta de luz"
            }
            """;

        String response = mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrair ID da resposta
        var responseObj = objectMapper.readTree(response);
        long contaId = responseObj.get("id").asLong();

        // Agora testar a alteração de situação com "Pago"
        String alteracaoJson = """
            {
              "situacao": "Pago",
              "dataPagamento": "2025-12-01"
            }
            """;

        mockMvc.perform(patch("/contas/" + contaId + "/situacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(alteracaoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("PAGO"))
                .andExpect(jsonPath("$.dataPagamento").value("2025-12-01"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveAlterarSituacaoComEnumNome() throws Exception {
        // Primeiro, criar uma conta
        String contaJson = """
            {
              "dataVencimento": "2025-12-31",
              "valor": 150.50,
              "descricao": "Conta de agua"
            }
            """;

        String response = mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrair ID da resposta
        var responseObj = objectMapper.readTree(response);
        long contaId = responseObj.get("id").asLong();

        // Agora testar a alteração de situação com "PAGO"
        String alteracaoJson = """
            {
              "situacao": "PAGO",
              "dataPagamento": "2025-12-01"
            }
            """;

        mockMvc.perform(patch("/contas/" + contaId + "/situacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(alteracaoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("PAGO"))
                .andExpect(jsonPath("$.dataPagamento").value("2025-12-01"));
    }
}
