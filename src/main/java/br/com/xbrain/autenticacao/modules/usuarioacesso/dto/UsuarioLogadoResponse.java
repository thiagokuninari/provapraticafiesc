package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogadoResponse {

    private Integer id;
    private String nome;
    private String email;
    private String fornecedorNome;
    private LocalDateTime dataLogin;

    public static UsuarioLogadoResponse of(Usuario usuario) {
        return UsuarioLogadoResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .fornecedorNome(usuario.getOrganizacaoEmpresa().getDescricao())
            .dataLogin(usuario.getDataUltimoAcesso())
            .build();
    }
}
