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

    public static UsuarioExcessoUsoResponse of(Integer usuarioId, Boolean bool) {
        return UsuarioExcessoUsoResponse
            .builder()
            .usuarioId(usuarioId)
            .bloqueado(bool)
            .build();
    }
}
