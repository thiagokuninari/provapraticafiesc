package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipeVendaUsuarioRequest {

    private Integer usuarioId;
    private String usuarioNome;
    private boolean trocaDeSubCanal;
    private boolean trocaDeNome;

    public static EquipeVendaUsuarioRequest of(Usuario usuario, boolean isTrocaDeSubCanal, boolean isTrocaDeNome) {
        return EquipeVendaUsuarioRequest.builder()
            .usuarioId(usuario.getId())
            .usuarioNome(usuario.getNome())
            .trocaDeSubCanal(isTrocaDeSubCanal)
            .trocaDeNome(isTrocaDeNome)
            .build();
    }
}
