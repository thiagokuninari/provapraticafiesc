package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UsuarioNomeResponse {

    private Integer id;
    private String nome;
    private ESituacao situacao;

    public UsuarioNomeResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public UsuarioNomeResponse(Integer id, String nome, ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.situacao = situacao;
    }

    public static UsuarioNomeResponse of(Usuario usuario) {
        return UsuarioNomeResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .situacao(usuario.getSituacao())
            .build();
    }

    public static UsuarioNomeResponse of(UsuarioEquipeDto usuarioEquipeDto) {
        return UsuarioNomeResponse.builder()
            .id(usuarioEquipeDto.getUsuarioId())
            .nome(usuarioEquipeDto.getUsuarioNome())
            .situacao(usuarioEquipeDto.getSituacao())
            .build();
    }
}
