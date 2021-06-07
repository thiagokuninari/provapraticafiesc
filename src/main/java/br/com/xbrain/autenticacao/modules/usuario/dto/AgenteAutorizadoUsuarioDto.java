package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgenteAutorizadoUsuarioDto {

    private int id;
    private String cnpj;
    private String razaoSocial;
    private List<Integer> usuarioId;

}
