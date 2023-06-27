package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NivelResponseTest {

    @Test
    public void of_deveRetornarNivelResponse_seSolicitado() {
        assertThat(NivelResponse.of(OrganizacaoEmpresaHelper.umNivel()))
            .extracting("id", "nome", "codigo")
            .containsExactly(1, "BACKOFFICE", CodigoNivel.BACKOFFICE.name());
    }
}
