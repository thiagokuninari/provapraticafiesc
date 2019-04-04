package br.com.xbrain.autenticacao.modules.comum.exception;

/**
 * Created by xbrain on 22/09/15.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String message) {
        super(message);
    }

    public ValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
