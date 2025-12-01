package com.contasapagar.contasapi.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para requisições de criação e atualização de contas.
 */
public class ContaRequestDto {

    @NotNull(message = "Data de vencimento é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valor;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    // Construtores
    public ContaRequestDto() {}

    public ContaRequestDto(LocalDate dataVencimento, BigDecimal valor, String descricao) {
        this.dataVencimento = dataVencimento;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "ContaRequestDto{" +
                "dataVencimento=" + dataVencimento +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
