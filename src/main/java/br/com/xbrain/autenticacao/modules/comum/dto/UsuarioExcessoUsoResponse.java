package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UsuarioExcessoUsoResponse {

    private Integer usuarioId;
    private boolean bloqueado;

    public static UsuarioExcessoUsoResponse of(Integer usuarioId, Boolean situacao) {
        return UsuarioExcessoUsoResponse
            .builder()
            .usuarioId(usuarioId)
            .bloqueado(situacao)
            .build();
    }
}
