package com.contapagar.infrastructure.repository;

import com.contapagar.domain.model.Conta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
class ContaRepositoryCustomImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Conta> filtrar(LocalDate dataVencimento, String descricao, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Conta> query = builder.createQuery(Conta.class);
        Root<Conta> root = query.from(Conta.class);

        List<Predicate> predicates = new ArrayList<>();

        if (dataVencimento != null) {
            predicates.add(builder.equal(root.get("dataVencimento"), dataVencimento));
        }

        if (descricao != null && !descricao.isEmpty()) {
            predicates.add(builder.like(
                    builder.lower(root.get("descricao")),
                    "%" + descricao.toLowerCase() + "%"
            ));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(builder.asc(root.get("dataVencimento")));

        List<Conta> result = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Conta> countRoot = countQuery.from(Conta.class);
        countQuery.select(builder.count(countRoot))
                .where(predicates.toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }


    public BigDecimal calcularTotalPago(LocalDate inicio, LocalDate fim) {
        return entityManager.createQuery(
                        "SELECT SUM(c.valor) FROM Conta c " +
                                "WHERE c.situacao = 'PAGA' " +
                                "AND c.dataPagamento BETWEEN :inicio AND :fim",
                        BigDecimal.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
    }
}