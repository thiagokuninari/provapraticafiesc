package br.com.xbrain.autenticacao.modules.importacao.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissaoEspecialImportacao {

    private Integer id;

    private Integer usuarioId;

    private Integer funcionalidadeId;

    private String role;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataBaixa;

    private Integer usuarioCadastroId;

    private Integer usuarioBaixaId;

}
