package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class AgenteAutorizadoUsuarioDto {

    private int id;
    private String cnpj;
    private String razaoSocial;
    private Integer usuarioId;

}
