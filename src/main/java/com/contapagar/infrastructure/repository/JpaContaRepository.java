package com.contapagar.infrastructure.repository;

import com.contapagar.domain.model.Conta;
import com.contapagar.domain.repository.ContaRepository;
import com.contapagar.domain.repository.ContaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaContaRepository extends JpaRepository<Conta, Long>, ContaRepository, ContaRepositoryCustom {

}
