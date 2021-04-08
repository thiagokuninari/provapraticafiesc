package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRemanejamentoRequest {

    private Integer usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private ESituacao usuarioSituacao;
    private Integer colaboradorVendasId;
    private Integer agenteAutorizadoId;

    public static UsuarioRemanejamentoRequest of(Usuario usuario, UsuarioMqRequest request) {
        return UsuarioRemanejamentoRequest
            .builder()
            .usuarioId(usuario.getId())
            .usuarioNome(usuario.getNome())
            .usuarioEmail(usuario.getEmail())
            .usuarioSituacao(usuario.getSituacao())
            .colaboradorVendasId(request.getColaboradorVendasId())
            .agenteAutorizadoId(request.getAgenteAutorizadoId())
            .build();
    }

    public static UsuarioRemanejamentoRequest of(UsuarioMqRequest request) {
        return UsuarioRemanejamentoRequest
            .builder()
            .usuarioNome(request.getNome())
            .usuarioEmail(request.getEmail())
            .colaboradorVendasId(request.getColaboradorVendasId())
            .agenteAutorizadoId(request.getAgenteAutorizadoId())
            .build();
    }
}
