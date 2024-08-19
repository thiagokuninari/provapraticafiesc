package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UsuarioNomeResponse {

    private Integer id;
    private String nome;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ESituacao situacao;

    public static UsuarioNomeResponse of(Integer id, String nome, ESituacao situacao) {
        return builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    public UsuarioNomeResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public UsuarioNomeResponse(Integer id, String nome, ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.situacao = situacao;
    }

    public static UsuarioNomeResponse of(UsuarioEquipeDto usuarioEquipeDto) {
        return UsuarioNomeResponse.builder()
            .id(usuarioEquipeDto.getUsuarioId())
            .nome(usuarioEquipeDto.getUsuarioNome())
            .situacao(usuarioEquipeDto.getSituacao())
            .build();
    }
}
