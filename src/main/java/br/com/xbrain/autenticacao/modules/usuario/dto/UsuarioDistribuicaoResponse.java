package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDistribuicaoResponse {

    private Integer id;
    private String nome;

    public static UsuarioDistribuicaoResponse of(EquipeVendaUsuarioResponse usuario) {
        return UsuarioDistribuicaoResponse
            .builder()
            .id(usuario.getUsuarioId())
            .nome(usuario.getUsuarioNome())
            .build();
    }
}
