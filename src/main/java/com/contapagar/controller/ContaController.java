package com.contapagar.controller;

import com.contapagar.application.ContaService;
import com.contapagar.controller.dto.ContaRequest;
import com.contapagar.domain.exception.RegistroNaoEncontradoException;
import com.contapagar.domain.exception.SituacaoInvalidaException;
import com.contapagar.domain.exception.DataPagamentoInvalidaException;
import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> criarConta(@RequestBody @Valid ContaRequest request) {
        Conta novaConta = contaService.criar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> obterContaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.obterPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<Conta>> listarContas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataVencimento,
            BigDecimal valor,

            @RequestParam(required = false)
            String descricao,

            Pageable pageable) {

        return ResponseEntity.ok(
               contaService.listarComFiltros(dataVencimento, valor, descricao, pageable)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> atualizarConta(
            @PathVariable Long id,
            @RequestBody @Valid ContaRequest request) {

        return ResponseEntity.ok(contaService.atualizar(id, request.toEntity()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Conta> atualizarSituacao(
            @PathVariable Long id,
            @RequestParam SituacaoConta situacao) {

        try {
            Conta contaAtualizada = contaService.atualizarSituacao(id, situacao);
            return ResponseEntity.ok(contaAtualizada);
        } catch (SituacaoInvalidaException | DataPagamentoInvalidaException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirConta(@PathVariable Long id) {
        contaService.excluir(id);
    }

    @GetMapping(value = "/total-pago", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> obterTotalPago(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        return ResponseEntity.ok(contaService.calcularTotalPagoPeriodo(dataInicial, dataFinal));
    }

    // Exception Handlers
    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<String> handleRegistroNaoEncontrado(RegistroNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({ SituacaoInvalidaException.class, DataPagamentoInvalidaException.class })
    public ResponseEntity<String> handleValidacaoException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
