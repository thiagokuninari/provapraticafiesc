package br.com.xbrain.autenticacao.modules.importacao.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioCidadeImportacao {

    private Integer cidadeId;

    private Integer usuarioId;

    private Integer usuarioCadastroId;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataBaixa;

    private Integer usuarioBaixaId;

}
