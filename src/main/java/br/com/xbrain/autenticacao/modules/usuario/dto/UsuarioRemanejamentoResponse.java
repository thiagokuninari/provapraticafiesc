package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioRemanejamentoResponse {

    private Integer usuarioId;
    private Integer colaboradorVendasId;
    private Integer agenteAutorizadoId;
}
