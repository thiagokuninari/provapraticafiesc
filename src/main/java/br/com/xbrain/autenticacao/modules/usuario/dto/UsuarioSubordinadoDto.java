package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSubordinadoDto {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private ESituacao situacao;
    private CodigoNivel codigoNivel;
    private CodigoDepartamento codigoDepartamento;
    private CodigoCargo codigoCargo;
    private String nomeCargo;
}
