package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioHierarquiaResponse {

    private Integer id;
    private String nome;
    private String cargoNome;
    private String name;
    private String status;

    public UsuarioHierarquiaResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.cargoNome = usuario.getCargo().getNome();
        this.name = usuario.getCargo().getNome().concat(" - ").concat(usuario.getNome());
        this.status = usuario.isAtivo() ? "ATIVO" : "INATIVO";
    }

    public static List<UsuarioHierarquiaResponse> convertTo(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioHierarquiaResponse::new)
                .collect(Collectors.toList());
    }
}
