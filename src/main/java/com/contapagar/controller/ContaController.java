package com.contapagar.controller;

import com.contapagar.application.ContaService;
import com.contapagar.controller.dto.ContaRequest;
import com.contapagar.domain.exception.RegistroNaoEncontradoException;
import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<Conta> criarConta(@RequestBody @Valid ContaRequest request) {
        Conta novaConta = contaService.criar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @GetMapping("/{id}")
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

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Conta> atualizarSituacao(
            @PathVariable Long id,
            @RequestParam SituacaoConta situacao) {

        return ResponseEntity.ok(contaService.atualizarSituacao(id, situacao));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirConta(@PathVariable Long id) {
        contaService.excluir(id);
    }

    @GetMapping("/total-pago")
    public ResponseEntity<BigDecimal> obterTotalPago(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(contaService.calcularTotalPagoPeriodo(inicio, fim));
    }

    // Exception Handler
    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<String> handleRegistroNaoEncontrado(
            RegistroNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
