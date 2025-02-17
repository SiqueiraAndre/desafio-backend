package com.contapagar.domain.repository;

import com.contapagar.domain.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ContaRepository extends JpaRepository<Conta, Long> {

//    @Query("SELECT c FROM Conta c " +
//            "WHERE (:id IS NULL OR c.id = :id) " +
//            "AND (:descricao IS NULL OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))")
//    Page<Conta> filtrar(@Param("dataVencimento") LocalDate dataVencimento,
//                        @Param("descricao") String descricao,
//                        Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Conta c " +
            "WHERE c.situacao = 'PAGA' " +
            "AND c.dataPagamento BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPago(@Param("inicio") LocalDate inicio,
                                 @Param("fim") LocalDate fim);
}
