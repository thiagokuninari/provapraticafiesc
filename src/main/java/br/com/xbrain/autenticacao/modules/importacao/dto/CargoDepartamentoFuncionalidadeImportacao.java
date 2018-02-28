package br.com.xbrain.autenticacao.modules.importacao.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CargoDepartamentoFuncionalidadeImportacao {

    private Integer id;

    private Integer cargoId;

    private Integer departamentoId;

    private Integer funcionalidadeId;

    private String role;

    private Integer empresaId;

    private Integer unidadeNegocioId;

    private Integer usuarioId;

    private LocalDateTime dataCadastro;

}
