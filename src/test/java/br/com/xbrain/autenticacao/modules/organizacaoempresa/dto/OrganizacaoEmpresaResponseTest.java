package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaResponseTest {

    @Test
    public void of_deveRetornarOrganizacaoEmpresaResponse_seSolicitado() {
        assertThat(OrganizacaoEmpresaResponse.of(umaOrganizacaoEmpresa()))
            .extracting("id", "razaoSocial", "cnpj", "modalidadesEmpresa", "nivel", "situacao")
            .containsExactly(1, "Organizacao 1", "19.427.182/0001-00",
                List.of(new SelectResponse(2, "TELEVENDAS"), new SelectResponse(1, "PAP")),
                new SelectResponse(1, "VAREJO"), ESituacaoOrganizacaoEmpresa.A);
    }

    private static OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa
            .builder()
            .id(1)
            .razaoSocial("Organizacao 1")
            .cnpj("19427182000100")
            .modalidadesEmpresa(List.of(umaModalidadeEmpresaTelevendas(), umaModalidadeEmpresaPap()))
            .nivel(Nivel.builder()
                .id(1)
                .codigo(CodigoNivel.VAREJO)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaPap() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(1);
        modalidadeEmpresa.setModalidadeEmpresa(EModalidadeEmpresa.PAP);
        return modalidadeEmpresa;
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaTelevendas() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(2);
        modalidadeEmpresa.setModalidadeEmpresa(EModalidadeEmpresa.TELEVENDAS);
        return modalidadeEmpresa;
    }
}
