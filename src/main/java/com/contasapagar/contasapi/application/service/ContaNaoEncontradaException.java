package com.contasapagar.contasapi.application.service;

/**
 * Exception lançada quando uma conta não é encontrada.
 */
public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException(Long id) {
        super("Conta não encontrada com ID: " + id);
    }

    public ContaNaoEncontradaException(String message) {
        super(message);
    }

    public ContaNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
