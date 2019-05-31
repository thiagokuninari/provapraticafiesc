package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

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
}
