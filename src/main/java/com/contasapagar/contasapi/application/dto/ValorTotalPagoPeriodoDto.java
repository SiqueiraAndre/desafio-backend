package com.contasapagar.contasapi.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta com valor total pago por período.
 */
public class ValorTotalPagoPeriodoDto {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private BigDecimal valorTotal;

    private Integer quantidadeContas;

    // Construtores
    public ValorTotalPagoPeriodoDto() {}

    public ValorTotalPagoPeriodoDto(LocalDate dataInicio, LocalDate dataFim,
                                   BigDecimal valorTotal, Integer quantidadeContas) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorTotal = valorTotal;
        this.quantidadeContas = quantidadeContas;
    }

    // Getters e Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getQuantidadeContas() {
        return quantidadeContas;
    }

    public void setQuantidadeContas(Integer quantidadeContas) {
        this.quantidadeContas = quantidadeContas;
    }

    @Override
    public String toString() {
        return "ValorTotalPagoPeriodoDto{" +
                "dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", valorTotal=" + valorTotal +
                ", quantidadeContas=" + quantidadeContas +
                '}';
    }
}
