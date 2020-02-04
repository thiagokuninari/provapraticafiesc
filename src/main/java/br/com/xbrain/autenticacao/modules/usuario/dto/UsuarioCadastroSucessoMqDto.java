package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioCadastroSucessoMqDto {
    private Integer geradorLeadsId;
    private Integer usuarioId;
    private Integer usuarioCadastroId;

    public static UsuarioCadastroSucessoMqDto of(Usuario usuario, UsuarioGeradorLeadsMqDto usuarioDto) {
        return UsuarioCadastroSucessoMqDto.builder()
            .geradorLeadsId(usuarioDto.getGeradorLeadsId())
            .usuarioId(usuario.getId())
            .usuarioCadastroId(usuario.getUsuarioCadastro().getId())
            .build();
    }
}
