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

    private Integer usuarioId; // Ver com o Diogo porque isso aqui era público
    private String usuarioNome;
    private boolean isTrocaDeSubCanal;
    private boolean isTrocaDeNome;

    public static EquipeVendaUsuarioRequest of(Usuario usuario, boolean isTrocaDeSubCanal, boolean isTrocaDeNome) {
        return EquipeVendaUsuarioRequest.builder()
            .usuarioId(usuario.getId())
            .usuarioNome(usuario.getNome())
            .isTrocaDeSubCanal(isTrocaDeSubCanal)
            .isTrocaDeNome(isTrocaDeNome)
            .build();
    }
}
