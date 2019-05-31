package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
public class UsuarioAgenteAutorizadoAgendamentoResponse {
    private Integer id;
    private String nome;
    private String equipeVendasNome;
    private String supervisorNome;

    public static UsuarioAgenteAutorizadoAgendamentoResponse of(Usuario usuario) {
        return UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .build();
    }

    public boolean isUsuarioSolicitante(Integer usuarioSolicitante) {
        return Objects.equals(usuarioSolicitante, id);
    }
}
