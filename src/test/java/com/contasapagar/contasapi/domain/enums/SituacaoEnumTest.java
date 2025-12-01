package com.contasapagar.contasapi.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste simples para validar o funcionamento do enum SituacaoEnum
 */
class SituacaoEnumTest {

    @Test
    void deveAceitarNomeDoEnum() {
        SituacaoEnum resultado = SituacaoEnum.fromString("PAGO");
        assertEquals(SituacaoEnum.PAGO, resultado);
    }

    @Test
    void deveAceitarDescricaoDoEnum() {
        SituacaoEnum resultado = SituacaoEnum.fromString("Pago");
        assertEquals(SituacaoEnum.PAGO, resultado);
    }

    @Test
    void deveAceitarCaseInsensitive() {
        SituacaoEnum resultado = SituacaoEnum.fromString("pago");
        assertEquals(SituacaoEnum.PAGO, resultado);
    }

    @Test
    void deveFalharComValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            SituacaoEnum.fromString("INVALIDO");
        });
    }

    @Test
    void deveFalharComValorNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            SituacaoEnum.fromString(null);
        });
    }

    @Test
    void deveFalharComStringVazia() {
        assertThrows(IllegalArgumentException.class, () -> {
            SituacaoEnum.fromString("");
        });
    }
}
