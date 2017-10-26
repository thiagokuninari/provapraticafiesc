package br.com.xbrain.autenticacao.modules.comum.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rhzeffa on 22/09/16.
 */
@ResponseStatus(
        value = HttpStatus.FORBIDDEN,
        reason = "Usuário sem permissão sobre a entidade requisitada.")
public class PermissaoException extends RuntimeException {

}
