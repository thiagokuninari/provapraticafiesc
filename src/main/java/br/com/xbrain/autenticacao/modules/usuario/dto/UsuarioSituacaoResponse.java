package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSituacaoResponse {

    private Integer id;
    private String nome;
    private ESituacao situacao;

    public static UsuarioSituacaoResponse of(UsuarioNomeResponse usuarioNomeResponse) {
        var usuarioSituacaoResponse = new UsuarioSituacaoResponse();
        BeanUtils.copyProperties(usuarioNomeResponse, usuarioSituacaoResponse);
        return usuarioSituacaoResponse;
    }
}
