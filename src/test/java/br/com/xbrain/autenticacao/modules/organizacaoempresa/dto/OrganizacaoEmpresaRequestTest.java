package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaRequestTest {

    @Test
    public void getCnpjSemMascara_deveRetornarCnpjSemMascara_quandoCnpjNotNull() {
        assertThat(OrganizacaoEmpresaHelper.organizacaoEmpresaRequest().getCnpjSemMascara())
            .isNotEmpty()
            .isEqualTo("66845365000125");
    }

    @Test
    public void getCnpjSemMascara_deveRetornarCnpjVazio_quandoCnpjNull() {
        assertThat(OrganizacaoEmpresaHelper.organizacaoEmpresaSemCnpjRequest().getCnpjSemMascara())
            .isEqualTo("");
    }
}
