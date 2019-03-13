package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEquipeVendasResponse {

    private Integer id;
    private Integer usuarioId;
    private String usuarioNome;
    private String cargoNome;

    public static UsuarioEquipeVendasResponse convertFrom(UsuarioResponse usuarioResponse) {
        UsuarioEquipeVendasResponse usuarioEquipeVendasResponse = new UsuarioEquipeVendasResponse();
        usuarioEquipeVendasResponse.setId(null);
        usuarioEquipeVendasResponse.setUsuarioId(usuarioResponse.getId());
        usuarioEquipeVendasResponse.setUsuarioNome(usuarioResponse.getNome());
        usuarioEquipeVendasResponse.setCargoNome(usuarioEquipeVendasResponse.getCargoNome());
        return usuarioEquipeVendasResponse;
    }

}
