package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UsuarioExcessoUsoResponse {

    private Integer usuarioId;
    private boolean bloqueado;

    public static UsuarioExcessoUsoResponse of(UsuarioParaDeslogar usuarioParaDeslogar) {
        return UsuarioExcessoUsoResponse
            .builder()
            .usuarioId(usuarioParaDeslogar.getUsuarioId())
            .bloqueado(Boolean.TRUE)
            .build();
    }
}
