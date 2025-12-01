package com.contasapagar.contasapi.application.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste específico para validar deserialização JSON dos DTOs
 */
@SpringBootTest
@ActiveProfiles("test")
class ContaRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveDeserializarJsonComFormatoIsoDate() throws Exception {
        // JSON no formato enviado pelo Swagger
        String json = """
            {
                "dataVencimento": "2025-12-01",
                "valor": 10.25,
                "descricao": "Conta de Gás"
            }
            """;

        // Configurar ObjectMapper para trabalhar com LocalDate
        objectMapper.findAndRegisterModules();

        // Act
        ContaRequestDto dto = objectMapper.readValue(json, ContaRequestDto.class);

        // Assert
        assertNotNull(dto);
        assertEquals(LocalDate.of(2025, 12, 1), dto.getDataVencimento());
        assertEquals(BigDecimal.valueOf(10.25), dto.getValor());
        assertEquals("Conta de Gás", dto.getDescricao());
    }

    @Test
    void deveValidarCamposObrigatorios() {
        ContaRequestDto dto = new ContaRequestDto();

        // Verifica que os campos são inicialmente null
        assertNull(dto.getDataVencimento());
        assertNull(dto.getValor());
        assertNull(dto.getDescricao());
    }
}
