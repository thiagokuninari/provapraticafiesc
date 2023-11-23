package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoCanalInternet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UsuarioAutenticadoTest {

    @Test
    public void isGerenteInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoGerenteCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_GERENTE)
            .isGerenteInternetOperacao())
            .isTrue();
    }

    @Test
    public void isGerenteInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoGerenteCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.GERENTE_OPERACAO)
            .isGerenteInternetOperacao())
            .isFalse();
    }

    @Test
    public void isSupervisorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoSupervisorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_SUPERVISOR)
            .isSupervisorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isSupervisorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoSupervisorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.SUPERVISOR_OPERACAO)
            .isSupervisorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isCoordenadorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoCoordenadorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_COORDENADOR)
            .isCoordenadorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isCoordenadorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoCoordenadorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.COORDENADOR_OPERACAO)
            .isCoordenadorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isVendedorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoVendedorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_VENDEDOR)
            .isVendedorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isVendedorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoVendedorCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.VENDEDOR_OPERACAO)
            .isVendedorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isBackofficeInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoBackofficeCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_BACKOFFICE)
            .isBackofficeInternetOperacao())
            .isTrue();
    }

    @Test
    public void isBackofficeInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoBackofficeCanalInternet() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.BACKOFFICE_GERENTE)
            .isBackofficeInternetOperacao())
            .isFalse();
    }
}
