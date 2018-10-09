package br.com.xbrain.autenticacao.modules.permissao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidTokenResetPassException extends RuntimeException {

    public InvalidTokenResetPassException(String message) {
        super(message);
    }
}
