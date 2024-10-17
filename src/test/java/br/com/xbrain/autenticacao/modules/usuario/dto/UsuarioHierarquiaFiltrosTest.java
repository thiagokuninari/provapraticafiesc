package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioHierarquiaFiltrosTest {

    @Test
    public void apenasSiteId_deveRetornarTrue_quandoCoordenadorIdEEquipeVendaIdNulos() {
        var filtros = UsuarioHierarquiaFiltros
            .builder()
            .siteId(123)
            .build();

        assertThat(filtros.apenasSiteId()).isTrue();
    }

    @Test
    public void apenasSiteId_deveRetornarFalse_quandoCoordenadorIdOuEquipeVendaIdNaoNulos() {
        var filtros = UsuarioHierarquiaFiltros
            .builder()
            .siteId(123)
            .coordenadorId(100)
            .equipeVendaId(100)
            .build();

        assertThat(filtros.apenasSiteId()).isFalse();
    }

    @Test
    public void apenasSiteId_deveRetornarFalse_quandoSiteIdNulo() {
        var filtros = UsuarioHierarquiaFiltros
            .builder()
            .siteId(null)
            .coordenadorId(100)
            .equipeVendaId(null)
            .build();

        assertThat(filtros.apenasSiteId()).isFalse();
    }
}
