package br.com.xbrain.autenticacao.modules.importacao.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioHierarquiaImportacao {

    private Integer usuarioId;

    private Integer usuarioSuperiorId;

    private LocalDateTime dataCadastro;

    private Integer usuarioCadastroId;

}
