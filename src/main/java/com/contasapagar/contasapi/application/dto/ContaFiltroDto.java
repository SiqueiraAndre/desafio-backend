package com.contasapagar.contasapi.application.dto;

import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * DTO para filtros de consulta de contas.
 */
public class ContaFiltroDto {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimentoInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimentoFim;

    private String descricao;

    private SituacaoEnum situacao;

    // Construtores
    public ContaFiltroDto() {}

    public ContaFiltroDto(LocalDate dataVencimentoInicio, LocalDate dataVencimentoFim,
                         String descricao, SituacaoEnum situacao) {
        this.dataVencimentoInicio = dataVencimentoInicio;
        this.dataVencimentoFim = dataVencimentoFim;
        this.descricao = descricao;
        this.situacao = situacao;
    }

    // Getters e Setters
    public LocalDate getDataVencimentoInicio() {
        return dataVencimentoInicio;
    }

    public void setDataVencimentoInicio(LocalDate dataVencimentoInicio) {
        this.dataVencimentoInicio = dataVencimentoInicio;
    }

    public LocalDate getDataVencimentoFim() {
        return dataVencimentoFim;
    }

    public void setDataVencimentoFim(LocalDate dataVencimentoFim) {
        this.dataVencimentoFim = dataVencimentoFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEnum situacao) {
        this.situacao = situacao;
    }

    /**
     * Verifica se há algum filtro ativo
     */
    public boolean temFiltros() {
        return dataVencimentoInicio != null ||
               dataVencimentoFim != null ||
               (descricao != null && !descricao.trim().isEmpty()) ||
               situacao != null;
    }

    @Override
    public String toString() {
        return "ContaFiltroDto{" +
                "dataVencimentoInicio=" + dataVencimentoInicio +
                ", dataVencimentoFim=" + dataVencimentoFim +
                ", descricao='" + descricao + '\'' +
                ", situacao=" + situacao +
                '}';
    }
}
