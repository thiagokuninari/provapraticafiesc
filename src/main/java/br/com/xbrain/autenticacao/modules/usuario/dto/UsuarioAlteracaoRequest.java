package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import lombok.Data;

@Data
public class UsuarioAlteracaoRequest {

    private Integer id;

    private String email;

    private CodigoCargo cargo;

}
