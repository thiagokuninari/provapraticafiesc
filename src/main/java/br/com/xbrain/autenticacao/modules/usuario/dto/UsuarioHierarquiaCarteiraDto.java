package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioHierarquiaCarteiraDto {
    private Integer usuarioId;
    private Integer usuarioSuperiorId;
    private Integer usuarioCadastroId;
}
