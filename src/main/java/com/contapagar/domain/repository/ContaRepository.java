package com.contapagar.domain.repository;

import com.contapagar.domain.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Page<Conta> filtrar(LocalDate dataVencimento, String descricao, Pageable pageable);

    BigDecimal calcularTotalPago(LocalDate inicio, LocalDate fim);
}