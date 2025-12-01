package com.contasapagar.contasapi.application.dto;

import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO para alteração de situação de conta.
 */
public class AlterarSituacaoDto {

    @NotNull(message = "Situação é obrigatória")
    private SituacaoEnum situacao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;

    // Construtores
    public AlterarSituacaoDto() {}

    public AlterarSituacaoDto(SituacaoEnum situacao, LocalDate dataPagamento) {
        this.situacao = situacao;
        this.dataPagamento = dataPagamento;
    }

    // Getters e Setters
    public SituacaoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEnum situacao) {
        this.situacao = situacao;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    /**
     * Valida se a data de pagamento é obrigatória quando a situação é PAGO
     */
    public void validar() {
        if (situacao == SituacaoEnum.PAGO && dataPagamento == null) {
            throw new IllegalArgumentException("Data de pagamento é obrigatória quando situação for PAGO");
        }
        // Para CANCELADO e PENDENTE, dataPagamento será ignorada/removida, então não é um erro
        if (situacao == SituacaoEnum.VENCIDO && dataPagamento != null) {
            throw new IllegalArgumentException("Data de pagamento não deve ser informada para situação VENCIDO");
        }
    }

    @Override
    public String toString() {
        return "AlterarSituacaoDto{" +
                "situacao=" + situacao +
                ", dataPagamento=" + dataPagamento +
                '}';
    }
}
