package com.contasapagar.contasapi.application.service;

import com.contasapagar.contasapi.application.dto.*;
import com.contasapagar.contasapi.application.mapper.ContaMapper;
import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Service responsável por todas as operações de negócio relacionadas às contas.
 */
@Service
@Transactional
public class ContaService {

    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;

    @Autowired
    public ContaService(ContaRepository contaRepository, ContaMapper contaMapper) {
        this.contaRepository = contaRepository;
        this.contaMapper = contaMapper;
    }

    /**
     * Cadastra uma nova conta
     */
    public ContaResponseDto cadastrar(ContaRequestDto contaRequest) {
        logger.info("Iniciando cadastro de nova conta: {}", contaRequest);

        try {
            Conta conta = contaMapper.toEntity(contaRequest);
            Conta contaSalva = contaRepository.save(conta);

            logger.info("Conta cadastrada com sucesso. ID: {}", contaSalva.getId());
            return contaMapper.toDto(contaSalva);

        } catch (Exception e) {
            logger.error("Erro ao cadastrar conta: {}", contaRequest, e);
            throw new RuntimeException("Erro ao cadastrar conta: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza uma conta existente
     */
    public ContaResponseDto atualizar(Long id, ContaRequestDto contaRequest) {
        logger.info("Iniciando atualização da conta ID: {} com dados: {}", id, contaRequest);

        try {
            Conta conta = buscarContaPorId(id);
            contaMapper.updateEntity(conta, contaRequest);

            Conta contaAtualizada = contaRepository.save(conta);

            logger.info("Conta atualizada com sucesso. ID: {}", id);
            return contaMapper.toDto(contaAtualizada);

        } catch (ContaNaoEncontradaException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao atualizar conta ID: {} com dados: {}", id, contaRequest, e);
            throw new RuntimeException("Erro ao atualizar conta: " + e.getMessage(), e);
        }
    }

    /**
     * Altera a situação de uma conta
     */
    public ContaResponseDto alterarSituacao(Long id, AlterarSituacaoDto alterarSituacaoDto) {
        logger.info("Alterando situação da conta ID: {} para: {}", id, alterarSituacaoDto);

        try {
            alterarSituacaoDto.validar();

            Conta conta = buscarContaPorId(id);

            switch (alterarSituacaoDto.getSituacao()) {
                case PAGO:
                    conta.marcarComoPago(alterarSituacaoDto.getDataPagamento());
                    break;
                case CANCELADO:
                    conta.cancelar();
                    break;
                case PENDENTE:
                    conta.voltarParaPendente();
                    break;
                default:
                    throw new IllegalArgumentException("Situação não suportada: " + alterarSituacaoDto.getSituacao());
            }

            Conta contaAtualizada = contaRepository.save(conta);

            logger.info("Situação da conta ID: {} alterada com sucesso para: {}", id, alterarSituacaoDto.getSituacao());
            return contaMapper.toDto(contaAtualizada);

        } catch (ContaNaoEncontradaException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erro de validação ao alterar situação da conta ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao alterar situação da conta ID: {}", id, e);
            throw new RuntimeException("Erro ao alterar situação da conta: " + e.getMessage(), e);
        }
    }

    /**
     * Busca conta por ID
     */
    @Transactional(readOnly = true)
    public ContaResponseDto buscarPorId(Long id) {
        logger.debug("Buscando conta por ID: {}", id);

        Conta conta = buscarContaPorId(id);
        return contaMapper.toDto(conta);
    }

    /**
     * Lista contas com filtros e paginação
     */
    @Transactional(readOnly = true)
    public Page<ContaResponseDto> listar(ContaFiltroDto filtro, Pageable pageable) {
        logger.info("Listando contas com filtros: {} e paginação: {}", filtro, pageable);

        try {
            Page<Conta> contasPage;

            // Temporariamente fazendo fallback para findAll para evitar problemas com filtros
            // TODO: Implementar filtros com Criteria API ou Query nativa funcional
            if (filtro.temFiltros()) {
                logger.warn("Filtros detectados, mas usando findAll temporariamente. Filtros: {}", filtro);
                contasPage = contaRepository.findAll(pageable);

                // Aplicar filtros em memória como solução temporária
                contasPage = aplicarFiltrosEmMemoria(contasPage, filtro, pageable);
            } else {
                contasPage = contaRepository.findAll(pageable);
            }

            logger.info("Encontradas {} contas", contasPage.getTotalElements());
            return contaMapper.toDto(contasPage);

        } catch (Exception e) {
            logger.error("Erro ao listar contas com filtros: {}", filtro, e);
            throw new RuntimeException("Erro ao listar contas: " + e.getMessage(), e);
        }
    }

    private Page<Conta> aplicarFiltrosEmMemoria(Page<Conta> contasPage, ContaFiltroDto filtro, Pageable pageable) {
        List<Conta> contasFiltradas = contasPage.getContent().stream()
                .filter(conta -> {
                    // Filtro por data de vencimento início
                    if (filtro.getDataVencimentoInicio() != null &&
                        conta.getDataVencimento().isBefore(filtro.getDataVencimentoInicio())) {
                        return false;
                    }

                    // Filtro por data de vencimento fim
                    if (filtro.getDataVencimentoFim() != null &&
                        conta.getDataVencimento().isAfter(filtro.getDataVencimentoFim())) {
                        return false;
                    }

                    // Filtro por descrição
                    if (filtro.getDescricao() != null && !filtro.getDescricao().trim().isEmpty()) {
                        if (!conta.getDescricao().toLowerCase().contains(filtro.getDescricao().toLowerCase())) {
                            return false;
                        }
                    }

                    // Filtro por situação
                    if (filtro.getSituacao() != null && !filtro.getSituacao().equals(conta.getSituacao())) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(contasFiltradas, pageable, contasFiltradas.size());
    }

    /**
     * Calcula valor total pago por período
     */
    @Transactional(readOnly = true)
    public ValorTotalPagoPeriodoDto calcularValorTotalPagoPorPeriodo(Periodo periodo) {
        logger.debug("Calculando valor total pago no período: {}", periodo);

        try {
            BigDecimal valorTotal = contaRepository.calcularValorTotalPagoPorPeriodo(
                    periodo.getDataInicio(),
                    periodo.getDataFim()
            );

            Integer quantidadeContas = contaRepository.contarContasPagasPorPeriodo(
                    periodo.getDataInicio(),
                    periodo.getDataFim()
            );

            ValorTotalPagoPeriodoDto resultado = contaMapper.toValorTotalPagoPeriodoDto(
                    periodo, valorTotal, quantidadeContas
            );

            logger.debug("Valor total calculado: {} para {} contas no período {}",
                    valorTotal, quantidadeContas, periodo);

            return resultado;

        } catch (Exception e) {
            logger.error("Erro ao calcular valor total pago por período: {}", periodo, e);
            throw new RuntimeException("Erro ao calcular valor total: " + e.getMessage(), e);
        }
    }

    /**
     * Remove uma conta
     */
    public void remover(Long id) {
        logger.info("Removendo conta ID: {}", id);

        try {
            Conta conta = buscarContaPorId(id);
            contaRepository.delete(conta);

            logger.info("Conta ID: {} removida com sucesso", id);

        } catch (ContaNaoEncontradaException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao remover conta ID: {}", id, e);
            throw new RuntimeException("Erro ao remover conta: " + e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar para buscar conta por ID com tratamento de exceção
     */
    private Conta buscarContaPorId(Long id) {
        Optional<Conta> contaOpt = contaRepository.findById(id);
        return contaOpt.orElseThrow(() -> new ContaNaoEncontradaException(id));
    }
}
