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

    public Integer usuarioId;
    public String usuarioNome;
    public boolean isTrocaDeSubCanal;

    public static EquipeVendaUsuarioRequest of(Usuario usuario) {
        return EquipeVendaUsuarioRequest.builder()
            .usuarioId(usuario.getId())
            .usuarioNome(usuario.getNome())
            .build();
    }
}
