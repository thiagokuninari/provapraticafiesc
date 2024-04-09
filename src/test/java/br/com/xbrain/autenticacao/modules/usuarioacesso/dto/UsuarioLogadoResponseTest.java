package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import org.junit.Test;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioOperadorBko;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.umUsuarioLogadoResponseBko;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioLogadoResponseTest {

    @Test
    public void setDadosResponse_deveAlterarOsDados_quandoSolicitado() {
        var response = umUsuarioLogadoResponseBko(4444);

        response.setDadosResponse(umUsuarioOperadorBko(4444, "Khada Jhin", "khadajhin4@teste.com"));

        assertThat(response)
            .extracting(UsuarioLogadoResponse::getUsuarioId, UsuarioLogadoResponse::getNome,
                UsuarioLogadoResponse::getEmail, UsuarioLogadoResponse::getFornecedorNome,
                UsuarioLogadoResponse::getDataEntrada)
            .containsExactlyInAnyOrder(4444, "Khada Jhin", "khadajhin4@teste.com", "Marcos AA",
                LocalDateTime.of(2024, 3, 22, 10, 30));
    }
}
