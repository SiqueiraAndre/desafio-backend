package com.contapagar.controller.dto;

import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContaRequest {

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataVencimento;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPagamento;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotNull
    private String descricao;

    @NotNull
    private SituacaoConta situacao;

    public ContaRequest(LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor, String descricao, SituacaoConta situacao) {
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.descricao = descricao;
        this.situacao = situacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
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

    public SituacaoConta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoConta situacao) {
        this.situacao = situacao;
    }

    public Conta toEntity() {
        Conta conta = new Conta();
        conta.setDataVencimento(dataVencimento);
        conta.setDataPagamento(dataPagamento);
        conta.setValor(valor);
        conta.setDescricao(descricao);
        conta.setSituacao(situacao);
        return conta;
    }
}
