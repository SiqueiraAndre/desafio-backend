package com.contapagar.domain.exception;

public class SituacaoInvalidaException extends RuntimeException {
    public SituacaoInvalidaException(String message) {
        super(message);
    }
}