package br.com.xbrain.autenticacao.modules.permissao.exception;

public class ExceedMaxTriesResetPassException extends RuntimeException {

    public ExceedMaxTriesResetPassException(String message) {
        super(message);
    }
}
