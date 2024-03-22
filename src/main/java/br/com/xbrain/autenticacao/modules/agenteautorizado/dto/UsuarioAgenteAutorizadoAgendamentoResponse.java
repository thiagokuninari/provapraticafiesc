package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
public class UsuarioAgenteAutorizadoAgendamentoResponse {
    private Integer id;
    private String nome;
    private Integer equipeVendasId;
    private String equipeVendasNome;
    private String supervisorNome;

    public static UsuarioAgenteAutorizadoAgendamentoResponse of(Usuario usuario) {
        return UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .build();
    }

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> of(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioAgenteAutorizadoAgendamentoResponse::of)
                .collect(Collectors.toList());
    }

    public boolean isUsuarioSolicitante(Integer usuarioSolicitante) {
        return Objects.equals(usuarioSolicitante, id);
    }
}
