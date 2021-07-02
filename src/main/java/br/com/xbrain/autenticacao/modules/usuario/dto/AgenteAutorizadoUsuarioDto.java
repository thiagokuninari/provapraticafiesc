package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAutorizadoUsuarioDto {

    private Integer usuarioId;
    private String cnpj;
    private String razaoSocial;

}
