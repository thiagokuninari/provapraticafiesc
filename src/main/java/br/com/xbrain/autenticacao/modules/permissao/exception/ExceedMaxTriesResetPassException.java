package br.com.xbrain.autenticacao.modules.permissao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.LOCKED)
public class ExceedMaxTriesResetPassException extends RuntimeException {

    public ExceedMaxTriesResetPassException(String message) {
        super(message);
    }
}
