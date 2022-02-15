package br.com.xbrain.autenticacao.modules.comum.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rhzeffa on 22/09/16.
 */
@NoArgsConstructor
@ResponseStatus(
        value = HttpStatus.FORBIDDEN,
        reason = "Usuário sem permissão sobre a entidade requisitada.")
public class PermissaoException extends RuntimeException {

    public PermissaoException(String message) {
        super(message);
    }
}
