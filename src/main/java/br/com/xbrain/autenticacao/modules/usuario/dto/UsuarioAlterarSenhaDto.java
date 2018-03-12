package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.Data;

@Data
public class UsuarioAlterarSenhaDto {

    private Integer usuarioId;

    private Eboolean alterarSenha;

}
