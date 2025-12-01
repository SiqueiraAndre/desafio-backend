package com.contasapagar.contasapi.domain.entity;

import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa uma conta a pagar no domínio.
 * Contém as regras de negócio relacionadas às operações com contas.
 */
@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoEnum situacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtor padrão para JPA
    protected Conta() {
        this.situacao = SituacaoEnum.PENDENTE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor para criação de nova conta
    public Conta(LocalDate dataVencimento, BigDecimal valor, String descricao) {
        this();
        validarDadosObrigatorios(dataVencimento, valor, descricao);
        this.dataVencimento = dataVencimento;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Métodos de negócio

    /**
     * Marca a conta como paga
     * @param dataPagamento Data em que o pagamento foi efetuado
     * @throws IllegalStateException se a conta não estiver em situação válida para pagamento
     */
    public void marcarComoPago(LocalDate dataPagamento) {
        if (this.situacao == SituacaoEnum.PAGO) {
            throw new IllegalStateException("Conta já está marcada como paga");
        }
        if (this.situacao == SituacaoEnum.CANCELADO) {
            throw new IllegalStateException("Conta cancelada não pode ser marcada como paga");
        }

        Objects.requireNonNull(dataPagamento, "Data de pagamento não pode ser nula");

        this.dataPagamento = dataPagamento;
        this.situacao = SituacaoEnum.PAGO;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancela a conta
     * @throws IllegalStateException se a conta já estiver paga
     */
    public void cancelar() {
        if (this.situacao == SituacaoEnum.PAGO) {
            throw new IllegalStateException("Conta já paga não pode ser cancelada");
        }
        if (this.situacao == SituacaoEnum.CANCELADO) {
            throw new IllegalStateException("Conta já está cancelada");
        }

        this.situacao = SituacaoEnum.CANCELADO;
        this.dataPagamento = null; // Remove data de pagamento se existir
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica se a conta está vencida
     * @return true se a conta estiver vencida
     */
    public boolean isVencida() {
        return this.situacao == SituacaoEnum.PENDENTE &&
               this.dataVencimento.isBefore(LocalDate.now());
    }

    /**
     * Atualiza a situação para vencida se necessário
     */
    public void verificarVencimento() {
        if (isVencida()) {
            this.situacao = SituacaoEnum.VENCIDO;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Atualiza os dados da conta
     */
    public void atualizar(LocalDate dataVencimento, BigDecimal valor, String descricao) {
        if (this.situacao == SituacaoEnum.PAGO) {
            throw new IllegalStateException("Conta já paga não pode ser alterada");
        }
        if (this.situacao == SituacaoEnum.CANCELADO) {
            throw new IllegalStateException("Conta cancelada não pode ser alterada");
        }

        validarDadosObrigatorios(dataVencimento, valor, descricao);

        this.dataVencimento = dataVencimento;
        this.valor = valor;
        this.descricao = descricao;
        this.updatedAt = LocalDateTime.now();

        // Reverifica se ainda está vencida após atualização
        if (this.situacao == SituacaoEnum.VENCIDO && !isVencida()) {
            this.situacao = SituacaoEnum.PENDENTE;
        }
    }

    private void validarDadosObrigatorios(LocalDate dataVencimento, BigDecimal valor, String descricao) {
        Objects.requireNonNull(dataVencimento, "Data de vencimento não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(descricao, "Descrição não pode ser nula");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode estar vazia");
        }

        if (descricao.length() > 500) {
            throw new IllegalArgumentException("Descrição não pode ter mais de 500 caracteres");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoEnum getSituacao() {
        return situacao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Equals e HashCode baseados no ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", dataVencimento=" + dataVencimento +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                ", situacao=" + situacao +
                '}';
    }
}
