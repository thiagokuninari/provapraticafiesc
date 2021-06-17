package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgenteAutorizadoUsuarioDto {

    private Integer usuarioId;
    private String cnpj;
    private String razaoSocial;

}
