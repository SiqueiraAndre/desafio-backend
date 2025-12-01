package com.contasapagar.contasapi.domain.enums;

/**
 * Enum que representa as possíveis situações de uma conta a pagar.
 */
public enum SituacaoEnum {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    VENCIDO("Vencido"),
    CANCELADO("Cancelado");

    private final String descricao;

    SituacaoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
