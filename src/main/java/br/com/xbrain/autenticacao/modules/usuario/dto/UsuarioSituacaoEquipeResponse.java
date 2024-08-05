package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSituacaoEquipeResponse {

    private Integer id;
    private String nome;
    private ESituacao situacaoUsuario;
    private Integer equipeVendasId;

}
