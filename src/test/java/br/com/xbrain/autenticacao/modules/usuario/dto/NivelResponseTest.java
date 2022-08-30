package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NivelResponseTest {

    @Test
    public void of_deveRetornarNivelResponse_seSolicitado() {
        assertThat(NivelResponse.of(OrganizacaoEmpresaHelper.umNivel()))
            .extracting("id", "nome")
            .containsExactly(1, "VAREJO");
    }
}
