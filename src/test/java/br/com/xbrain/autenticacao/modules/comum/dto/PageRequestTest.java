package br.com.xbrain.autenticacao.modules.comum.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRequestTest {

    @Test
    public void pageRequest_deveMontarPageRequestVazio_quandoParametrosVazios() {
        var pageRequest = new PageRequest();

        assertEquals(pageRequest, new PageRequest());
    }

    @Test
    public void pageRequest_deveMontarPageRequestCompleto_quandoTiverParametros() {
        var pageRequest = new PageRequest(1, 12, "ASC", "LEFT");

        assertEquals(pageRequest, new PageRequest(1, 12, "ASC", "LEFT"));
    }

    @Test
    public void getPage_deveRetornarONumeroDePaginas_quandoSolicitado() {
        var pageRequest = new PageRequest();
        pageRequest.setPage(1);

        assertEquals(pageRequest.getPage(), 1);
    }

    @Test
    public void getSize_deveRetornarOTamanhoDaPagina_quandoSolicitado() {
        var pageRequest = new PageRequest();
        pageRequest.setSize(12);

        assertEquals(pageRequest.getSize(), 12);
    }

    @Test
    public void getOrderBy_deveRetornarOTipoDeOrdenacaoDaPagina_quandoSolicitado() {
        var pageRequest = new PageRequest();
        pageRequest.setOrderBy("ASC");

        assertEquals(pageRequest.getOrderBy(), "ASC");
    }

    @Test
    public void setOrderDirection_deveRetornarOTipoDeOrdenacaoDaPagina_quandoSolicitado() {
        var pageRequest = new PageRequest();
        pageRequest.setOrderDirection("DESC");

        assertEquals(pageRequest.getOrderDirection(), "DESC");
    }
}
