package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicoAlvoComunicadoFiltrosTest {

    @Test
    public void adicionarUsuariosId_deveAdicionarIdsAosUsuarios_quandoUsuariosIdsForNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        assertThat(filtro.getUsuariosIds()).isNull();
        filtro.adicionarUsuariosId(List.of(1, 2));
        assertThat(filtro.getUsuariosIds()).isEqualTo(List.of(1, 2));
    }

    @Test
    public void adicionarUsuariosId_naoDeveAdicionarIdsAosUsuarios_quandoUsuariosIdNovosForNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        filtro.setUsuariosIds(List.of(4, 5));
        assertThat(filtro.getUsuariosIds()).isNotNull();
        filtro.adicionarUsuariosId(null);
        assertThat(filtro.getUsuariosIds()).isEqualTo(List.of(4, 5));
    }

    @Test
    public void haveFiltrosDeLocalizacao_deveRetornarTrue_quandoCidadesIdsNaoForNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        filtro.setCidadesIds(List.of(4, 5));
        assertThat(filtro.haveFiltrosDeLocalizacao()).isTrue();
    }

    @Test
    public void haveFiltrosDeLocalizacao_deveRetornarTrue_quandoUfIdNaoForNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        filtro.setUfId(5);
        assertThat(filtro.haveFiltrosDeLocalizacao()).isTrue();
    }

    @Test
    public void haveFiltrosDeLocalizacao_deveRetornarTrue_quandoRegionalIdNaoForNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        filtro.setRegionalId(5);
        assertThat(filtro.haveFiltrosDeLocalizacao()).isTrue();
    }

    @Test
    public void haveFiltrosDeLocalizacao_deveRetornarFalso_quandoRegionalIdEUfIdECidadesIdsForemNull() {
        var filtro = new PublicoAlvoComunicadoFiltros();
        assertThat(filtro.haveFiltrosDeLocalizacao()).isFalse();
    }
}
