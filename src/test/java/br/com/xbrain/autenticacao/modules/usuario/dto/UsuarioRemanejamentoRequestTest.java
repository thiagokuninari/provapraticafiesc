package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistoricoTest.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioRemanejamentoRequestTest {

    @Test
    public void of_deveRetornarUsuarioRemanejamentoRequest_quandoReceberUsuarioEUsuarioMqRequestEUsuarioAntigoId() {
        var usuarioMqRequest = umUsuarioMqRequest();
        var usuario = umUsuario();
        usuario.setEmail("usuarioteste@gmail.com");
        usuario.setSituacao(ESituacao.A);
        assertThat(UsuarioRemanejamentoRequest.of(usuario, usuarioMqRequest, 1))
            .extracting(UsuarioRemanejamentoRequest::getUsuarioId, UsuarioRemanejamentoRequest::getUsuarioNome,
                UsuarioRemanejamentoRequest::getUsuarioEmail, UsuarioRemanejamentoRequest::getUsuarioAntigoId,
                UsuarioRemanejamentoRequest::getUsuarioSituacao, UsuarioRemanejamentoRequest::getAgenteAutorizadoId,
                UsuarioRemanejamentoRequest::getColaboradorVendasId
            )
            .containsExactly(100, "Thiago", "usuarioteste@gmail.com", 1, ESituacao.A, 5, 6);
    }

    @Test
    public void of_deveRetornarUsuarioRemanejamentoRequest_quandoReceberUsuarioMqRequest() {
        var usuarioMqRequest = umUsuarioMqRequest();
        assertThat(UsuarioRemanejamentoRequest.of(usuarioMqRequest))
            .extracting(UsuarioRemanejamentoRequest::getUsuarioNome, UsuarioRemanejamentoRequest::getUsuarioEmail,
                UsuarioRemanejamentoRequest::getColaboradorVendasId, UsuarioRemanejamentoRequest::getAgenteAutorizadoId
            )
            .containsExactly("Usuário Teste", "usuarioteste@gmail.com", 6, 5);
    }

    private static UsuarioMqRequest umUsuarioMqRequest() {
        return UsuarioMqRequest
            .builder()
            .id(1)
            .nome("Usuário Teste")
            .agenteAutorizadoId(5)
            .colaboradorVendasId(6)
            .email("usuarioteste@gmail.com")
            .build();
    }

}
