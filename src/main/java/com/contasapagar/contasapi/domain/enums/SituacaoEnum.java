package com.contasapagar.contasapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    @Override
    public String toString() {
        // Serializa como nome em UPPERCASE para compatibilidade com testes e contratos
        return name();
    }

    /**
     * Método para deserialização JSON case-insensitive
     * Aceita tanto o nome do enum (PAGO) quanto a descrição (Pago)
     */
    @JsonCreator
    public static SituacaoEnum fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Situação não pode ser nula ou vazia");
        }

        String normalizedValue = value.trim().toUpperCase();

        // Primeiro tenta encontrar pelo nome do enum
        try {
            return SituacaoEnum.valueOf(normalizedValue);
        } catch (IllegalArgumentException e) {
            // Se não encontrou, tenta pela descrição
            for (SituacaoEnum situacao : values()) {
                if (situacao.descricao.equalsIgnoreCase(value.trim()) ||
                    situacao.name().equalsIgnoreCase(value.trim())) {
                    return situacao;
                }
            }
            throw new IllegalArgumentException("Situação inválida: '" + value +
                "'. Valores aceitos: PENDENTE, PAGO, VENCIDO, CANCELADO (ou suas descrições)");
        }
    }
}
