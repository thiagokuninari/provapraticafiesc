package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSiteResponse {

    private Integer usuarioId;
    private String usuarioNome;
    private String cargoNome;

    public static UsuarioSiteResponse of(UsuarioResponse usuarioResponse) {
        return UsuarioSiteResponse
            .builder()
            .usuarioId(usuarioResponse.getId())
            .usuarioNome(usuarioResponse.getNome())
            .cargoNome(usuarioResponse.getCodigoCargo().name())
            .build();
    }
}
