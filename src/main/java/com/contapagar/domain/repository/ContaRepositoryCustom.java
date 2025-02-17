package com.contapagar.domain.repository;

import com.contapagar.domain.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ContaRepositoryCustom {

    Page<Conta> filtrar(LocalDate dataVencimento, String descricao, Pageable pageable);

    BigDecimal calcularTotalPago(LocalDate inicio, LocalDate fim);
}