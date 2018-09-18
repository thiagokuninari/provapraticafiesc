package br.com.xbrain.autenticacao.modules.permissao.exception;

public class InvalidTokenResetPassException extends RuntimeException {

    public InvalidTokenResetPassException(String message) {
        super(message);
    }
}
