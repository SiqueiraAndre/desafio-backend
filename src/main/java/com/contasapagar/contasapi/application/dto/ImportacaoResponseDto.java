package com.contasapagar.contasapi.application.dto;

import java.util.List;

/**
 * DTO para resposta de importação de contas via CSV.
 */
public class ImportacaoResponseDto {

    private Integer totalLinhas;
    private Integer sucessos;
    private Integer erros;
    private List<String> mensagensErro;

    // Construtores
    public ImportacaoResponseDto() {}

    public ImportacaoResponseDto(Integer totalLinhas, Integer sucessos,
                               Integer erros, List<String> mensagensErro) {
        this.totalLinhas = totalLinhas;
        this.sucessos = sucessos;
        this.erros = erros;
        this.mensagensErro = mensagensErro;
    }

    // Getters e Setters
    public Integer getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(Integer totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public Integer getSucessos() {
        return sucessos;
    }

    public void setSucessos(Integer sucessos) {
        this.sucessos = sucessos;
    }

    public Integer getErros() {
        return erros;
    }

    public void setErros(Integer erros) {
        this.erros = erros;
    }

    public List<String> getMensagensErro() {
        return mensagensErro;
    }

    public void setMensagensErro(List<String> mensagensErro) {
        this.mensagensErro = mensagensErro;
    }

    public boolean temErros() {
        return erros != null && erros > 0;
    }

    @Override
    public String toString() {
        return "ImportacaoResponseDto{" +
                "totalLinhas=" + totalLinhas +
                ", sucessos=" + sucessos +
                ", erros=" + erros +
                ", mensagensErro=" + mensagensErro +
                '}';
    }
}
