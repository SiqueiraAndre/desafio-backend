package com.contapagar.domain.exception;

public class RegistroNaoEncontradoException extends RuntimeException {
    public RegistroNaoEncontradoException(Long id) {
        super("Conta não encontrada com ID: " + id);
    }
}