package com.contasapagar.contasapi.presentation.controller;

import com.contasapagar.contasapi.application.dto.*;
import com.contasapagar.contasapi.application.service.ContaService;
import com.contasapagar.contasapi.application.service.ContaNaoEncontradaException;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller REST para gerenciamento de contas a pagar.
 * Implementa todas as operações CRUD e consultas específicas.
 */
@RestController
@RequestMapping("/contas")
@Tag(name = "Contas", description = "API para gerenciamento de contas a pagar")
public class ContaController {

    private static final Logger logger = LoggerFactory.getLogger(ContaController.class);

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    /**
     * Cadastra uma nova conta
     */
    @PostMapping
    @Operation(summary = "Cadastrar nova conta", description = "Cria uma nova conta a pagar no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ContaResponseDto> cadastrar(
            @Parameter(description = "Dados da conta a ser criada", required = true)
            @Valid @RequestBody ContaRequestDto contaRequest) {

        logger.info("Recebida requisição para cadastrar conta: {}", contaRequest);

        ContaResponseDto contaResponse = contaService.cadastrar(contaRequest);

        logger.info("Conta cadastrada com sucesso. ID: {}", contaResponse.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(contaResponse);
    }

    /**
     * Atualiza uma conta existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta", description = "Atualiza os dados de uma conta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ContaResponseDto> atualizar(
            @Parameter(description = "ID da conta", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novos dados da conta", required = true)
            @Valid @RequestBody ContaRequestDto contaRequest) {

        logger.info("Recebida requisição para atualizar conta ID: {} com dados: {}", id, contaRequest);

        ContaResponseDto contaResponse = contaService.atualizar(id, contaRequest);

        logger.info("Conta ID: {} atualizada com sucesso", id);

        return ResponseEntity.ok(contaResponse);
    }

    /**
     * Altera a situação de uma conta
     */
    @PatchMapping("/{id}/situacao")
    @Operation(summary = "Alterar situação da conta", description = "Altera a situação de uma conta (pago, cancelado, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Situação alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito - situação inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ContaResponseDto> alterarSituacao(
            @Parameter(description = "ID da conta", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nova situação da conta", required = true)
            @Valid @RequestBody AlterarSituacaoDto alterarSituacaoDto) {

        logger.info("Recebida requisição para alterar situação da conta ID: {} para: {}", id, alterarSituacaoDto);
        logger.debug("Detalhes do DTO: situacao={}, dataPagamento={}",
                    alterarSituacaoDto.getSituacao(), alterarSituacaoDto.getDataPagamento());

        ContaResponseDto contaResponse = contaService.alterarSituacao(id, alterarSituacaoDto);
        logger.info("Situação da conta ID: {} alterada com sucesso", id);
        return ResponseEntity.ok(contaResponse);
    }

    /**
     * Busca uma conta por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna uma conta específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ContaResponseDto> buscarPorId(
            @Parameter(description = "ID da conta", required = true, example = "1")
            @PathVariable Long id) {

        logger.debug("Recebida requisição para buscar conta ID: {}", id);

        ContaResponseDto contaResponse = contaService.buscarPorId(id);

        return ResponseEntity.ok(contaResponse);
    }

    /**
     * Lista contas com filtros e paginação
     */
    @GetMapping
    @Operation(summary = "Listar contas", description = "Lista contas com filtros opcionais e paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ContaResponseDto>> listar(
            @Parameter(description = "Data inicial do filtro de vencimento (formato: dd/MM/yyyy)", example = "01/01/2024")
            @RequestParam(required = false) String dataVencimentoInicioStr,

            @Parameter(description = "Data final do filtro de vencimento (formato: dd/MM/yyyy)", example = "31/12/2024")
            @RequestParam(required = false) String dataVencimentoFimStr,

            @Parameter(description = "Filtro de descrição (busca parcial)", example = "Conta de luz")
            @RequestParam(required = false) String descricao,

            @Parameter(description = "Filtro de situação", example = "PENDENTE")
            @RequestParam(required = false) String situacao,

            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamanho da página", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo para ordenação. Exemplos: 'dataVencimento', 'valor,desc', 'situacao,asc'",
                      example = "dataVencimento,asc")
            @RequestParam(defaultValue = "dataVencimento,asc") String sort) {

        logger.info("Recebida requisição para listar contas - Parâmetros: dataInicio='{}', dataFim='{}', descricao='{}', situacao='{}', page={}, size={}, sort='{}'",
                   dataVencimentoInicioStr, dataVencimentoFimStr, descricao, situacao, page, size, sort);

        ContaFiltroDto filtro = new ContaFiltroDto();

        // Construir Sort a partir do parâmetro string
        Sort sortObj = Sort.by("dataVencimento").ascending(); // padrão
        if (sort != null && !sort.trim().isEmpty()) {
            try {
                String[] sortParts = sort.split(",");
                String field = sortParts[0].trim();
                Sort.Direction direction = Sort.Direction.ASC;

                if (sortParts.length > 1) {
                    String directionStr = sortParts[1].trim().toUpperCase();
                    if ("DESC".equals(directionStr)) {
                        direction = Sort.Direction.DESC;
                    }
                }

                sortObj = Sort.by(direction, field);
            } catch (Exception e) {
                logger.warn("Erro ao processar parâmetro sort: {}. Usando ordenação padrão.", sort);
                sortObj = Sort.by("dataVencimento").ascending();
            }
        }

        // Construir Pageable
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Converter strings de data para LocalDate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (dataVencimentoInicioStr != null && !dataVencimentoInicioStr.trim().isEmpty()) {
            try {
                logger.debug("Parsing data inicio: '{}'", dataVencimentoInicioStr);
                filtro.setDataVencimentoInicio(LocalDate.parse(dataVencimentoInicioStr, dateFormatter));
                logger.debug("Data inicio parsed com sucesso: {}", filtro.getDataVencimentoInicio());
            } catch (Exception e) {
                logger.error("Erro ao fazer parse da data de vencimento inicial: '{}'. Erro: {}",
                           dataVencimentoInicioStr, e.getMessage());
                throw new IllegalArgumentException("Data de vencimento inicial inválida: '" +
                                                 dataVencimentoInicioStr + "'. Use o formato dd/MM/yyyy. Exemplo: 01/01/2024");
            }
        }

        if (dataVencimentoFimStr != null && !dataVencimentoFimStr.trim().isEmpty()) {
            try {
                logger.debug("Parsing data fim: '{}'", dataVencimentoFimStr);
                filtro.setDataVencimentoFim(LocalDate.parse(dataVencimentoFimStr, dateFormatter));
                logger.debug("Data fim parsed com sucesso: {}", filtro.getDataVencimentoFim());
            } catch (Exception e) {
                logger.error("Erro ao fazer parse da data de vencimento final: '{}'. Erro: {}",
                           dataVencimentoFimStr, e.getMessage());
                throw new IllegalArgumentException("Data de vencimento final inválida: '" +
                                                 dataVencimentoFimStr + "'. Use o formato dd/MM/yyyy. Exemplo: 31/12/2024");
            }
        }

        filtro.setDescricao(descricao);

        // Converte string para enum se fornecida
        if (situacao != null && !situacao.trim().isEmpty()) {
            try {
                filtro.setSituacao(com.contasapagar.contasapi.domain.enums.SituacaoEnum.valueOf(situacao.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Situação inválida: " + situacao + ". Valores válidos: PENDENTE, PAGO, VENCIDO, CANCELADO");
            }
        }

        Page<ContaResponseDto> contas = contaService.listar(filtro, pageable);

        logger.debug("Retornando {} contas de um total de {}", contas.getNumberOfElements(), contas.getTotalElements());

        return ResponseEntity.ok(contas);
    }

    /**
     * Calcula valor total pago por período
     */
    @GetMapping("/total-pago")
    @Operation(summary = "Calcular valor total pago por período",
               description = "Retorna o valor total pago e quantidade de contas pagas em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ValorTotalPagoPeriodoDto> calcularValorTotalPago(
            @Parameter(description = "Data de início do período", required = true, example = "01/01/2024")
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,

            @Parameter(description = "Data de fim do período", required = true, example = "31/01/2024")
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim) {

        logger.info("Recebida requisição para calcular valor total pago entre {} e {}", dataInicio, dataFim);

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior ou igual à data de fim");
        }

        Periodo periodo = new Periodo(dataInicio, dataFim);
        ValorTotalPagoPeriodoDto resultado = contaService.calcularValorTotalPagoPorPeriodo(periodo);

        logger.info("Valor total calculado: {} para período de {} a {}",
                resultado.getValorTotal(), dataInicio, dataFim);

        return ResponseEntity.ok(resultado);
    }

    /**
     * Remove uma conta
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover conta", description = "Remove uma conta do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID da conta", required = true, example = "1")
            @PathVariable Long id) {

        logger.info("Recebida requisição para remover conta ID: {}", id);

        contaService.remover(id);

        logger.info("Conta ID: {} removida com sucesso", id);

        return ResponseEntity.noContent().build();
    }
}
