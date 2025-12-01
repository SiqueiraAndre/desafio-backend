package com.contasapagar.contasapi.application.service;

/**
 * Exception lançada quando há erro na importação de arquivo CSV.
 */
public class ImportacaoException extends RuntimeException {

    public ImportacaoException(String message) {
        super(message);
    }

    public ImportacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
