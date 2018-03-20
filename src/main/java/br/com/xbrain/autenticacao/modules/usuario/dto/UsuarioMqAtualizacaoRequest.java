package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsuarioMqAtualizacaoRequest {

    private List<Integer> empresasIds;
    private Integer unidadeId;
    private List<Integer> usuariosIds;
    private String exception;
}
