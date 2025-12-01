package com.contasapagar.contasapi.application.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste para validar deserialização do enum SituacaoEnum
 */
public class AlterarSituacaoDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void deveDesserializarSituacaoComDescricao() throws Exception {
        String json = """
            {
              "situacao": "Pago",
              "dataPagamento": "2025-12-01"
            }
            """;

        AlterarSituacaoDto dto = objectMapper.readValue(json, AlterarSituacaoDto.class);

        assertNotNull(dto);
        assertEquals(com.contasapagar.contasapi.domain.enums.SituacaoEnum.PAGO, dto.getSituacao());
        assertEquals(LocalDate.of(2025, 12, 1), dto.getDataPagamento());
    }

    @Test
    void deveDesserializarSituacaoComNomeEnum() throws Exception {
        String json = """
            {
              "situacao": "PAGO",
              "dataPagamento": "2025-12-01"
            }
            """;

        AlterarSituacaoDto dto = objectMapper.readValue(json, AlterarSituacaoDto.class);

        assertNotNull(dto);
        assertEquals(com.contasapagar.contasapi.domain.enums.SituacaoEnum.PAGO, dto.getSituacao());
        assertEquals(LocalDate.of(2025, 12, 1), dto.getDataPagamento());
    }

    @Test
    void deveDesserializarSituacaoCaseInsensitive() throws Exception {
        String json = """
            {
              "situacao": "pago",
              "dataPagamento": "2025-12-01"
            }
            """;

        AlterarSituacaoDto dto = objectMapper.readValue(json, AlterarSituacaoDto.class);

        assertNotNull(dto);
        assertEquals(com.contasapagar.contasapi.domain.enums.SituacaoEnum.PAGO, dto.getSituacao());
        assertEquals(LocalDate.of(2025, 12, 1), dto.getDataPagamento());
    }

    @Test
    void deveFalharComSituacaoInvalida() {
        String json = """
            {
              "situacao": "INVALIDA",
              "dataPagamento": "2025-12-01"
            }
            """;

        assertThrows(Exception.class, () -> {
            objectMapper.readValue(json, AlterarSituacaoDto.class);
        });
    }
}
