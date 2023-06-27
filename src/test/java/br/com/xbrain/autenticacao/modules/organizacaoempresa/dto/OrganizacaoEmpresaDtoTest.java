package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaDtoTest {

    @Test
    public void of_deveRetornarOrganizacaoEmpresa_seSolicitado() {
        assertThat(OrganizacaoEmpresaDto.of(umaOrganizacaoEmpresa()))
            .extracting("id", "nome", "situacao", "codigo")
            .containsExactly(1, "Organizacao 1", ESituacaoOrganizacaoEmpresa.A, "codigo");
    }

    private static OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa
            .builder()
            .id(1)
            .nome("Organizacao 1")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .codigo("codigo")
            .build();
    }
}
