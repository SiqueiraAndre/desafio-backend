package com.contasapagar.contasapi.infrastructure.repository;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositório JPA para a entidade Conta.
 * Extends JpaRepository para operações básicas de CRUD.
 */
@Repository
public interface ContaJpaRepository extends JpaRepository<Conta, Long> {

    /**
     * Busca contas por situação
     */
    List<Conta> findBySituacao(SituacaoEnum situacao);

    /**
     * Busca contas por data de vencimento
     */
    List<Conta> findByDataVencimento(LocalDate dataVencimento);

    /**
     * Busca contas vencidas (PENDENTE e data de vencimento anterior a hoje)
     */
    @Query("SELECT c FROM Conta c WHERE c.situacao = 'PENDENTE' AND c.dataVencimento < CURRENT_DATE")
    List<Conta> findContasVencidas();

    /**
     * Busca contas por período de vencimento
     */
    @Query("SELECT c FROM Conta c WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    List<Conta> findByPeriodoVencimento(@Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim);

    /**
     * Calcula o total pago em um período
     */
    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Conta c " +
           "WHERE c.situacao = 'PAGO' " +
           "AND c.dataPagamento BETWEEN :dataInicio AND :dataFim")
    BigDecimal calcularTotalPagoPorPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                          @Param("dataFim") LocalDate dataFim);

    /**
     * Conta registros por situação
     */
    long countBySituacao(SituacaoEnum situacao);

    /**
     * Conta as contas pagas em um período
     */
    @Query("SELECT COUNT(c) FROM Conta c " +
           "WHERE c.situacao = 'PAGO' " +
           "AND c.dataPagamento BETWEEN :dataInicio AND :dataFim")
    Integer contarContasPagasPorPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim);

    /**
     * Busca contas com filtros dinâmicos
     */
    @Query(value = "SELECT * FROM contas c WHERE " +
           "(?1 IS NULL OR c.data_vencimento >= ?1) AND " +
           "(?2 IS NULL OR c.data_vencimento <= ?2) AND " +
           "(?3 IS NULL OR ?3 = '' OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', ?3, '%'))) AND " +
           "(?4 IS NULL OR c.situacao::text = ?4)",
           nativeQuery = true)
    Page<Conta> findByFiltros(LocalDate dataVencimentoInicio,
                             LocalDate dataVencimentoFim,
                             String descricao,
                             String situacao,
                             Pageable pageable);
}
