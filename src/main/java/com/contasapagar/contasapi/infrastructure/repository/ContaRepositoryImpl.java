package com.contasapagar.contasapi.infrastructure.repository;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de domínio usando JPA.
 * Faz a ponte entre a camada de domínio e a infraestrutura.
 */
@Component
public class ContaRepositoryImpl implements ContaRepository {

    private final ContaJpaRepository jpaRepository;
    private final ContaCustomRepository customRepository;

    @Autowired
    public ContaRepositoryImpl(ContaJpaRepository jpaRepository, ContaCustomRepository customRepository) {
        this.jpaRepository = jpaRepository;
        this.customRepository = customRepository;
    }

    @Override
    public Conta save(Conta conta) {
        return jpaRepository.save(conta);
    }

    @Override
    public Optional<Conta> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Conta> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Conta> findByFiltros(LocalDate dataVencimentoInicio,
                                    LocalDate dataVencimentoFim,
                                    String descricao,
                                    SituacaoEnum situacao,
                                    Pageable pageable) {
        return customRepository.findByFiltrosDinamicos(dataVencimentoInicio, dataVencimentoFim,
                                                      descricao, situacao, pageable);
    }

    @Override
    public List<Conta> findBySituacao(SituacaoEnum situacao) {
        return jpaRepository.findBySituacao(situacao);
    }

    @Override
    public BigDecimal calcularTotalPagoPorPeriodo(Periodo periodo) {
        return calcularTotalPagoPorPeriodo(periodo.getDataInicio(), periodo.getDataFim());
    }

    @Override
    public BigDecimal calcularTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return jpaRepository.calcularTotalPagoPorPeriodo(dataInicio, dataFim);
    }

    @Override
    public Page<Conta> findWithFilters(LocalDate dataVencimentoInicio,
                                      LocalDate dataVencimentoFim,
                                      String descricao,
                                      SituacaoEnum situacao,
                                      Pageable pageable) {
        return findByFiltros(dataVencimentoInicio, dataVencimentoFim, descricao, situacao, pageable);
    }

    @Override
    public BigDecimal calcularValorTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return calcularTotalPagoPorPeriodo(dataInicio, dataFim);
    }

    @Override
    public Integer contarContasPagasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return jpaRepository.contarContasPagasPorPeriodo(dataInicio, dataFim);
    }

    @Override
    public List<Conta> findContasVencidas() {
        return jpaRepository.findContasVencidas();
    }

    @Override
    public List<Conta> findByDataVencimento(LocalDate dataVencimento) {
        return jpaRepository.findByDataVencimento(dataVencimento);
    }

    @Override
    public List<Conta> findByPeriodoVencimento(Periodo periodo) {
        return jpaRepository.findByPeriodoVencimento(periodo.getDataInicio(), periodo.getDataFim());
    }

    @Override
    public void delete(Conta conta) {
        jpaRepository.delete(conta);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countBySituacao(SituacaoEnum situacao) {
        return jpaRepository.countBySituacao(situacao);
    }
}
