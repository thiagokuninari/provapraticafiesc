package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSocialHubRequestMq {

    private Integer id;
    private String nome;
    private String email;
    private String nivel;
    private String cargo;
    private List<Integer> regionaisIds;
    private Integer territorioMercadoDesenvolvimentoId;
    private boolean isPermissaoAdmSocialRemovida;
    private ESituacao situacao;

    public static UsuarioSocialHubRequestMq from(Usuario usuario, List<Integer> regionaisIds, String nomeCargo,
                                                 boolean isPermissaoAdmSocialRemovida) {
        return UsuarioSocialHubRequestMq.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .nivel(usuario.getNivelCodigo() != null ? usuario.getNivelCodigo().toString() : null)
            .cargo(nomeCargo)
            .regionaisIds(regionaisIds)
            .territorioMercadoDesenvolvimentoId(usuario.getTerritorioMercadoDesenvolvimentoIdOrNull())
            .isPermissaoAdmSocialRemovida(isPermissaoAdmSocialRemovida)
            .build();
    }

    public static UsuarioSocialHubRequestMq from(Integer usuarioId, boolean isPermissaoAdmSocialRemovida) {
        var request = new UsuarioSocialHubRequestMq();
        request.setId(usuarioId);
        request.setPermissaoAdmSocialRemovida(isPermissaoAdmSocialRemovida);
        return request;
    }

    public static UsuarioSocialHubRequestMq from(Integer usuarioId, ESituacao situacao) {
        var request = new UsuarioSocialHubRequestMq();
        request.setId(usuarioId);
        request.setSituacao(situacao);
        return request;
    }
}
