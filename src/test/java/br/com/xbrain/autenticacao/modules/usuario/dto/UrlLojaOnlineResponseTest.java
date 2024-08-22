package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlLojaOnlineResponseTest {

    @Test
    public void of_deveRetornarUrlLojaOnlineResponse_quandoSolicitado() {
        assertThat(UrlLojaOnlineResponse.of(umUsuario()))
            .extracting("urlLojaBase", "urlLojaProspect", "urlLojaProspectNextel", "cupomLojaOnline")
            .containsExactly("url/loja/base", "url/prospect", "url/prospect/nextel", "cupom");
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .urlLojaBase("url/loja/base")
            .urlLojaProspect("url/prospect")
            .urlLojaProspectNextel("url/prospect/nextel")
            .cupomLoja("cupom")
            .build();
    }
}
