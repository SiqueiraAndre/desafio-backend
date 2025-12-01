package com.contasapagar.contasapi.infrastructure.repository;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório customizado usando Criteria API para queries dinâmicas
 */
@Repository
public class ContaCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Conta> findByFiltrosDinamicos(LocalDate dataVencimentoInicio,
                                             LocalDate dataVencimentoFim,
                                             String descricao,
                                             SituacaoEnum situacao,
                                             Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Conta> query = cb.createQuery(Conta.class);
        Root<Conta> conta = query.from(Conta.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro por data de vencimento início
        if (dataVencimentoInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(conta.get("dataVencimento"), dataVencimentoInicio));
        }

        // Filtro por data de vencimento fim
        if (dataVencimentoFim != null) {
            predicates.add(cb.lessThanOrEqualTo(conta.get("dataVencimento"), dataVencimentoFim));
        }

        // Filtro por descrição (case insensitive e parcial)
        if (descricao != null && !descricao.trim().isEmpty()) {
            predicates.add(cb.like(
                cb.lower(conta.get("descricao")),
                "%" + descricao.toLowerCase() + "%"
            ));
        }

        // Filtro por situação
        if (situacao != null) {
            predicates.add(cb.equal(conta.get("situacao"), situacao));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Aplicar ordenação do Pageable
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    query.orderBy(cb.asc(conta.get(order.getProperty())));
                } else {
                    query.orderBy(cb.desc(conta.get(order.getProperty())));
                }
            });
        } else {
            // Ordenação padrão
            query.orderBy(cb.asc(conta.get("dataVencimento")));
        }

        // Executar query com paginação
        List<Conta> contas = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Contar total de elementos
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Conta> countRoot = countQuery.from(Conta.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(contas, pageable, total);
    }
}
