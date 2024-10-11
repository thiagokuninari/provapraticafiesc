package br.com.xbrain.autenticacao.modules.comum.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PageRequestTest {

    @Test
    public void pageRequest_deveMontarPageRequestVazio_quandoParametrosVazios() {
        assertThat(new PageRequest())
            .extracting(PageRequest::getPage, PageRequest::getSize, PageRequest::getOrderBy, PageRequest::getOrderDirection)
            .containsExactly(0, 10, "id", "ASC");
    }

    @Test
    public void pageRequest_deveMontarPageRequestCompleto_quandoTiverParametros() {
        assertThat(new PageRequest(1, 44, "nome", "DESC"))
            .extracting(PageRequest::getPage, PageRequest::getSize, PageRequest::getOrderBy, PageRequest::getOrderDirection)
            .containsExactly(1, 44, "nome", "DESC");
    }

    @Test
    public void next_deveRetornarNulo_quandoSolicitado() {
        var pageRequest = new PageRequest(1, 44, "nome", "DESC");

        assertThat(pageRequest.next()).isNull();
    }

    @Test
    public void previousOrFirst_deveRetornarNulo_quandoSolicitado() {
        var pageRequest = new PageRequest(1, 44, "nome", "DESC");

        assertThat(pageRequest.previousOrFirst()).isNull();
    }

    @Test
    public void first_deveRetornarNulo_quandoSolicitado() {
        var pageRequest = new PageRequest(1, 44, "nome", "DESC");

        assertThat(pageRequest.first()).isNull();
    }

    @Test
    public void hasPrevious_deveRetornarFalse_quandoSolicitado() {
        var pageRequest = new PageRequest(1, 44, "nome", "DESC");

        assertThat(pageRequest.hasPrevious()).isFalse();
    }
}
