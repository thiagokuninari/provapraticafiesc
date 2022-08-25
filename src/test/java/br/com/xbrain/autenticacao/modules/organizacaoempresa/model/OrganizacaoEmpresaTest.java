package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganizacaoEmpresaTest {

    @Test
    public void of_deveRetornarObjetoCorreto_quandoRecebeOrganizacaoEmpresa() {
        assertThat(OrganizacaoEmpresa.of(umaOrganizacaoEmpresaRequest(), 1, Nivel.builder().id(1).build(),
            List.of(umaModalidadeEmpresaPap(), umaModalidadeEmpresaTelevendas())))
            .extracting("razaoSocial", "cnpj", "nivel", "modalidadesEmpresa", "situacao")
            .containsExactly("Organizacao 1", "08112392000192", umNivel(), List.of(umaModalidadeEmpresaPap(),
                umaModalidadeEmpresaTelevendas()), ESituacaoOrganizacaoEmpresa.A);
    }

    @Test
    public void getNivelIdNome_deveRetornarVazio_quandoNivelNull() {
        assertThat(umaOrganizacaoEmpresa().getNivelIdNome()).isEmpty();
    }

    @Test
    public void getNivelIdNome_deveRetornarIdENome_quandoNivelNotNull() {
        assertThat(umaOutraOrganizacaoEmpresa().getNivelIdNome())
            .isPresent()
            .hasValue(SelectResponse.of(1, CodigoNivel.VAREJO.name()));
    }

    @Test
    public void getModalidadesEmpresaIdNome_deveRetornarEmpty_quandoModalidadesEmpresaIsEmpty() {
        assertThat(umaOrganizacaoEmpresa().getModalidadesEmpresaIdNome()).isEmpty();
    }

    @Test
    public void getModalidadesEmpresaIdNome_deveRetornarModalidadesEmpresa_quandoModalidadesEmpresaIsNotEmpty() {
        assertThat(umaOutraOrganizacaoEmpresa().getModalidadesEmpresaIdNome())
            .isNotEmpty()
            .isEqualTo(List.of(SelectResponse.of(1, EModalidadeEmpresa.PAP.name()),
                SelectResponse.of(2, EModalidadeEmpresa.TELEVENDAS.name())));
    }

    @Test
    public void isAtivo_deveRetornarTrue_quandoOrganizacaoAtiva() {
        assertTrue(umaOrganizacaoComStatus(1, ESituacaoOrganizacaoEmpresa.A)
            .isAtivo());
    }

    @Test
    public void isAtivo_deveRetornarFalse_quandoOrganizacaoInativa() {
        assertFalse(umaOrganizacaoComStatus(1, ESituacaoOrganizacaoEmpresa.I)
            .isAtivo());
    }

    private OrganizacaoEmpresa umaOrganizacaoComStatus(Integer id, ESituacaoOrganizacaoEmpresa situacao) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .situacao(situacao)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .razaoSocial("Organizacao 1")
            .cnpj("08112392000192")
            .nivelId(1)
            .modalidadesEmpresaIds(List.of(1, 2))
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .razaoSocial("Organizacao 1")
            .cnpj("08112392000192")
            .modalidadesEmpresa(null)
            .nivel(null)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    private OrganizacaoEmpresa umaOutraOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .razaoSocial("Organizacao 1")
            .cnpj("08112392000192")
            .nivel(Nivel.builder()
                .id(1)
                .codigo(CodigoNivel.VAREJO)
                .build())
            .modalidadesEmpresa(List.of(umaModalidadeEmpresaPap(), umaModalidadeEmpresaTelevendas()))
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

    public static Nivel umNivel() {
        var nivel = new Nivel();
        nivel.setId(1);
        nivel.setCodigo(null);
        return nivel;
    }
}
