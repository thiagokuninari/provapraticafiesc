package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioEquipeVendasDto {

    private Integer usuarioId;
    private String usuarioNome;
    private String cargoNome;

    public static UsuarioEquipeVendasDto createFromUsuario(Usuario usuario) {
        return new UsuarioEquipeVendasDto(usuario.getId(), usuario.getNome(), usuario.getCargo().getNome());
    }

}
