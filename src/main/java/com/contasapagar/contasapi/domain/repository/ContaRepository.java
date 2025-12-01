package com.contasapagar.contasapi.domain.repository;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.enums.SituacaoEnum;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de Conta seguindo os princípios DDD.
 * Define os contratos para persistência da entidade Conta.
 */
public interface ContaRepository {

    /**
     * Salva uma conta no repositório
     * @param conta Conta a ser salva
     * @return Conta salva com ID gerado
     */
    Conta save(Conta conta);

    /**
     * Busca uma conta por ID
     * @param id ID da conta
     * @return Optional com a conta se encontrada
     */
    Optional<Conta> findById(Long id);

    /**
     * Busca todas as contas com paginação
     * @param pageable Informações de paginação
     * @return Página com as contas
     */
    Page<Conta> findAll(Pageable pageable);

    /**
     * Busca contas aplicando filtros opcionais
     * @param dataVencimentoInicio Data inicial do filtro de vencimento (opcional)
     * @param dataVencimentoFim Data final do filtro de vencimento (opcional)
     * @param descricao Filtro de descrição (opcional, busca parcial)
     * @param situacao Filtro de situação (opcional)
     * @param pageable Informações de paginação
     * @return Página com as contas filtradas
     */
    Page<Conta> findByFiltros(LocalDate dataVencimentoInicio,
                             LocalDate dataVencimentoFim,
                             String descricao,
                             SituacaoEnum situacao,
                             Pageable pageable);

    /**
     * Busca contas por situação
     * @param situacao Situação das contas
     * @return Lista de contas com a situação especificada
     */
    List<Conta> findBySituacao(SituacaoEnum situacao);

    /**
     * Calcula o valor total pago em um período
     * @param periodo Período para cálculo
     * @return Valor total pago no período
     */
    BigDecimal calcularTotalPagoPorPeriodo(Periodo periodo);

    /**
     * Calcula o valor total pago em um período específico
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Valor total pago no período
     */
    BigDecimal calcularTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca contas vencidas (situação PENDENTE e data de vencimento anterior a hoje)
     * @return Lista de contas vencidas
     */
    List<Conta> findContasVencidas();

    /**
     * Busca contas que vencem em uma data específica
     * @param dataVencimento Data de vencimento
     * @return Lista de contas que vencem na data especificada
     */
    List<Conta> findByDataVencimento(LocalDate dataVencimento);

    /**
     * Busca contas por período de vencimento
     * @param periodo Período de vencimento
     * @return Lista de contas no período
     */
    List<Conta> findByPeriodoVencimento(Periodo periodo);

    /**
     * Remove uma conta do repositório
     * @param conta Conta a ser removida
     */
    void delete(Conta conta);

    /**
     * Remove uma conta por ID
     * @param id ID da conta a ser removida
     */
    void deleteById(Long id);

    /**
     * Verifica se existe uma conta com o ID especificado
     * @param id ID da conta
     * @return true se a conta existir
     */
    boolean existsById(Long id);

    /**
     * Conta o total de registros
     * @return Quantidade total de contas
     */
    long count();

    /**
     * Conta contas por situação
     * @param situacao Situação das contas
     * @return Quantidade de contas na situação especificada
     */
    long countBySituacao(SituacaoEnum situacao);
}
