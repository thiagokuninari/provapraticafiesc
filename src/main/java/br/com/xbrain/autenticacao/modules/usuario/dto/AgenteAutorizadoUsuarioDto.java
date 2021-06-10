package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class AgenteAutorizadoUsuarioDto {

    private Integer usuarioId;
    private String cnpj;
    private String razaoSocial;

}
