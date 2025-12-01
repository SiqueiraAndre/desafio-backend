package com.contasapagar.contasapi.presentation.controller;

import com.contasapagar.contasapi.application.dto.AlterarSituacaoDto;
import com.contasapagar.contasapi.application.dto.ContaRequestDto;
import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ContaController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("ContaController Integration Tests")
class ContaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContaRepository contaRepository;

    private ContaRequestDto contaRequestDto;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @BeforeEach
    void setUp() {
        contaRequestDto = new ContaRequestDto(
                LocalDate.of(2024, 12, 31),
                BigDecimal.valueOf(150.50),
                "Conta de luz - Teste Integration"
        );
    }

    @Nested
    @DisplayName("Cadastrar Conta")
    class CadastrarContaTest {

        @Test
        @DisplayName("Deve cadastrar conta com usuário admin")
        @WithMockUser(roles = "ADMIN")
        void deveCadastrarContaComUsuarioAdmin() throws Exception {
            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.dataVencimento").value("31/12/2024"))
                    .andExpect(jsonPath("$.valor").value(150.50))
                    .andExpect(jsonPath("$.descricao").value("Conta de luz - Teste Integration"))
                    .andExpect(jsonPath("$.situacao").value("PENDENTE"))
                    .andExpect(jsonPath("$.dataPagamento").isEmpty())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @Test
        @DisplayName("Deve cadastrar conta com usuário comum")
        @WithMockUser(roles = "USER")
        void deveCadastrarContaComUsuarioComum() throws Exception {
            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaRequestDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve falhar sem autenticação")
        void deveFalharSemAutenticacao() throws Exception {
            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaRequestDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve validar dados obrigatórios")
        @WithMockUser(roles = "ADMIN")
        void deveValidarDadosObrigatorios() throws Exception {
            ContaRequestDto contaInvalida = new ContaRequestDto(null, null, null);

            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaInvalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors").isArray())
                    .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("Deve validar valor mínimo")
        @WithMockUser(roles = "ADMIN")
        void deveValidarValorMinimo() throws Exception {
            ContaRequestDto contaValorZero = new ContaRequestDto(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.ZERO,
                    "Conta com valor zero"
            );

            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaValorZero)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve validar tamanho máximo da descrição")
        @WithMockUser(roles = "ADMIN")
        void deveValidarTamanhoMaximoDescricao() throws Exception {
            String descricaoLonga = "a".repeat(501);
            ContaRequestDto contaDescricaoLonga = new ContaRequestDto(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    descricaoLonga
            );

            mockMvc.perform(post("/contas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaDescricaoLonga)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Buscar Conta")
    class BuscarContaTest {

        @Test
        @DisplayName("Deve buscar conta existente por ID")
        @WithMockUser(roles = "USER")
        void deveBuscarContaExistentePorId() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta de luz"
            ));

            // Act & Assert
            mockMvc.perform(get("/contas/{id}", conta.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(conta.getId()))
                    .andExpect(jsonPath("$.descricao").value("Conta de luz"));
        }

        @Test
        @DisplayName("Deve retornar 404 para conta inexistente")
        @WithMockUser(roles = "USER")
        void deveRetornar404ParaContaInexistente() throws Exception {
            mockMvc.perform(get("/contas/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrada")));
        }
    }

    @Nested
    @DisplayName("Listar Contas")
    class ListarContasTest {

        @Test
        @DisplayName("Deve listar contas com paginação")
        @WithMockUser(roles = "USER")
        void deveListarContasComPaginacao() throws Exception {
            // Arrange
            contaRepository.save(new Conta(LocalDate.of(2024, 12, 31), BigDecimal.valueOf(150.50), "Conta 1"));
            contaRepository.save(new Conta(LocalDate.of(2024, 11, 30), BigDecimal.valueOf(89.90), "Conta 2"));

            // Act & Assert
            mockMvc.perform(get("/contas")
                            .param("size", "10")
                            .param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.number").value(0));
        }

        @Test
        @DisplayName("Deve filtrar contas por descrição")
        @WithMockUser(roles = "USER")
        void deveFiltrarContasPorDescricao() throws Exception {
            // Arrange
            contaRepository.save(new Conta(LocalDate.of(2024, 12, 31), BigDecimal.valueOf(150.50), "Conta de luz"));
            contaRepository.save(new Conta(LocalDate.of(2024, 11, 30), BigDecimal.valueOf(89.90), "Conta de água"));

            // Act & Assert
            mockMvc.perform(get("/contas")
                            .param("descricao", "luz"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].descricao").value(containsString("luz")));
        }

        @Test
        @DisplayName("Deve filtrar contas por período de vencimento")
        @WithMockUser(roles = "USER")
        @DirtiesContext
        void deveFiltrarContasPorPeriodoVencimento() throws Exception {
            // Arrange
            contaRepository.save(new Conta(LocalDate.of(2024, 12, 31), BigDecimal.valueOf(150.50), "Conta dezembro"));
            contaRepository.save(new Conta(LocalDate.of(2024, 6, 15), BigDecimal.valueOf(89.90), "Conta junho"));

            // Act & Assert
            mockMvc.perform(get("/contas")
                            .param("dataVencimentoInicioStr", "01/12/2024")
                            .param("dataVencimentoFimStr", "31/12/2024"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].descricao").value("Conta dezembro"));
        }

        @Test
        @DisplayName("Deve filtrar contas por situação")
        @WithMockUser(roles = "USER")
        void deveFiltrarContasPorSituacao() throws Exception {
            // Arrange
            Conta contaPendente = contaRepository.save(new Conta(LocalDate.of(2024, 12, 31), BigDecimal.valueOf(150.50), "Conta pendente"));
            Conta contaPaga = contaRepository.save(new Conta(LocalDate.of(2024, 11, 30), BigDecimal.valueOf(89.90), "Conta paga"));
            contaPaga.marcarComoPago(LocalDate.of(2024, 12, 1));
            contaRepository.save(contaPaga);

            // Act & Assert
            mockMvc.perform(get("/contas")
                            .param("situacao", "PENDENTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].situacao").value("PENDENTE"));
        }

        @Test
        @DisplayName("Deve validar situação inválida")
        @WithMockUser(roles = "USER")
        void deveValidarSituacaoInvalida() throws Exception {
            mockMvc.perform(get("/contas")
                            .param("situacao", "SITUACAO_INEXISTENTE"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Situação inválida")));
        }
    }

    @Nested
    @DisplayName("Atualizar Conta")
    class AtualizarContaTest {

        @Test
        @DisplayName("Deve atualizar conta existente")
        @WithMockUser(roles = "ADMIN")
        void deveAtualizarContaExistente() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta original"
            ));

            ContaRequestDto contaAtualizada = new ContaRequestDto(
                    LocalDate.of(2025, 1, 15),
                    BigDecimal.valueOf(200.00),
                    "Conta atualizada"
            );

            // Act & Assert
            mockMvc.perform(put("/contas/{id}", conta.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaAtualizada)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(conta.getId()))
                    .andExpect(jsonPath("$.dataVencimento").value("15/01/2025"))
                    .andExpect(jsonPath("$.valor").value(200.00))
                    .andExpect(jsonPath("$.descricao").value("Conta atualizada"));
        }

        @Test
        @DisplayName("Deve falhar ao atualizar conta inexistente")
        @WithMockUser(roles = "ADMIN")
        void deveFalharAoAtualizarContaInexistente() throws Exception {
            mockMvc.perform(put("/contas/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contaRequestDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Alterar Situação")
    class AlterarSituacaoTest {

        @Test
        @DisplayName("Deve alterar situação para PAGO")
        @WithMockUser(roles = "USER")
        void deveAlterarSituacaoParaPago() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta a ser paga"
            ));

            AlterarSituacaoDto alterarSituacao = new AlterarSituacaoDto(
                    SituacaoEnum.PAGO,
                    LocalDate.of(2024, 12, 15)
            );

            // Act & Assert
            mockMvc.perform(patch("/contas/{id}/situacao", conta.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSituacao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.situacao").value("PAGO"))
                    .andExpect(jsonPath("$.dataPagamento").value("15/12/2024"));
        }

        @Test
        @DisplayName("Deve alterar situação para CANCELADO")
        @WithMockUser(roles = "USER")
        void deveAlterarSituacaoParaCancelado() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta a ser cancelada"
            ));

            AlterarSituacaoDto alterarSituacao = new AlterarSituacaoDto(
                    SituacaoEnum.CANCELADO,
                    null
            );

            // Act & Assert
            mockMvc.perform(patch("/contas/{id}/situacao", conta.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSituacao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.situacao").value("CANCELADO"))
                    .andExpect(jsonPath("$.dataPagamento").isEmpty());
        }

        @Test
        @DisplayName("Deve validar data de pagamento obrigatória para PAGO")
        @WithMockUser(roles = "USER")
        void deveValidarDataPagamentoObrigatParaPago() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta teste"
            ));

            AlterarSituacaoDto alterarSituacao = new AlterarSituacaoDto(
                    SituacaoEnum.PAGO,
                    null  // Data de pagamento nula
            );

            // Act & Assert
            mockMvc.perform(patch("/contas/{id}/situacao", conta.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSituacao)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Calcular Total Pago")
    class CalcularTotalPagoTest {

        @Test
        @DisplayName("Deve calcular valor total pago por período")
        @WithMockUser(roles = "USER")
        void deveCalcularValorTotalPagoPorPeriodo() throws Exception {
            // Arrange
            Conta conta1 = new Conta(LocalDate.of(2024, 12, 15), BigDecimal.valueOf(150.50), "Conta 1");
            conta1.marcarComoPago(LocalDate.of(2024, 12, 20));
            contaRepository.save(conta1);

            Conta conta2 = new Conta(LocalDate.of(2024, 12, 25), BigDecimal.valueOf(89.90), "Conta 2");
            conta2.marcarComoPago(LocalDate.of(2024, 12, 25));
            contaRepository.save(conta2);

            // Conta não paga (não deve ser incluída)
            contaRepository.save(new Conta(LocalDate.of(2024, 12, 30), BigDecimal.valueOf(100.00), "Conta não paga"));

            // Act & Assert
            mockMvc.perform(get("/contas/total-pago")
                            .param("dataInicio", "01/12/2024")
                            .param("dataFim", "31/12/2024"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dataInicio").value("01/12/2024"))
                    .andExpect(jsonPath("$.dataFim").value("31/12/2024"))
                    .andExpect(jsonPath("$.valorTotal").value(240.40))
                    .andExpect(jsonPath("$.quantidadeContas").value(2));
        }

        @Test
        @DisplayName("Deve validar parâmetros obrigatórios")
        @WithMockUser(roles = "USER")
        void deveValidarParametrosObrigatorios() throws Exception {
            mockMvc.perform(get("/contas/total-pago"))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(get("/contas/total-pago")
                            .param("dataInicio", "01/12/2024"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve validar ordem das datas")
        @WithMockUser(roles = "USER")
        void deveValidarOrdemDatas() throws Exception {
            mockMvc.perform(get("/contas/total-pago")
                            .param("dataInicio", "31/12/2024")
                            .param("dataFim", "01/12/2024"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Data de início deve ser anterior")));
        }
    }

    @Nested
    @DisplayName("Remover Conta")
    class RemoverContaTest {

        @Test
        @DisplayName("Deve remover conta como admin")
        @WithMockUser(roles = "ADMIN")
        void deveRemoverContaComoAdmin() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta a ser removida"
            ));

            // Act & Assert
            mockMvc.perform(delete("/contas/{id}", conta.getId()))
                    .andExpect(status().isNoContent());

            // Verificar se foi removida
            mockMvc.perform(get("/contas/{id}", conta.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve falhar ao remover conta como usuário comum")
        @WithMockUser(roles = "USER")
        void deveFalharAoRemoverContaComoUsuarioComum() throws Exception {
            // Arrange
            Conta conta = contaRepository.save(new Conta(
                    LocalDate.of(2024, 12, 31),
                    BigDecimal.valueOf(150.50),
                    "Conta protegida"
            ));

            // Act & Assert
            mockMvc.perform(delete("/contas/{id}", conta.getId()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar remover conta inexistente")
        @WithMockUser(roles = "ADMIN")
        void deveRetornar404AoTentarRemoverContaInexistente() throws Exception {
            mockMvc.perform(delete("/contas/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }
}
