package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
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

    @Test
    public void isAssistenteOperacao_deveRetornarTrue_seUsuarioAutenticadoForCargoAssistenteOperacao() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isAssistenteOperacao())
            .isTrue();
    }

    @Test
    public void isAssistenteOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoAssistenteOperacao() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.GERENTE_OPERACAO)
            .isAssistenteOperacao())
            .isFalse();
    }

    @Test
    public void isGerenteOperacao_deveRetornarTrue_seUsuarioAutenticadoForCargoGerenteOperacao() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.GERENTE_OPERACAO)
            .isGerenteOperacao())
            .isTrue();
    }

    @Test
    public void isGerenteOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoGerenteOperacao() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isGerenteOperacao())
            .isFalse();
    }

    @Test
    public void isSocioPrincipal_deveRetornarTrue_seUsuarioAutenticadoForCargoAgenteAutorizadoSocio() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .isSocioPrincipal())
            .isTrue();
    }

    @Test
    public void isSocioPrincipal_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoAgenteAutorizadoSocio() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isSocioPrincipal())
            .isFalse();
    }

    @Test
    public void isExecutivo_deveRetornarTrue_seUsuarioAutenticadoForCargoExecutivo() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.EXECUTIVO)
            .isExecutivo())
            .isTrue();
    }

    @Test
    public void isExecutivo_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoExecutivo() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isExecutivo())
            .isFalse();
    }

    @Test
    public void isExecutivoHunter_deveRetornarTrue_seUsuarioAutenticadoForCargoExecutivoHunter() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.EXECUTIVO_HUNTER)
            .isExecutivoHunter())
            .isTrue();
    }

    @Test
    public void isExecutivoHunter_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoExecutivoHunter() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_GERENTE)
            .isExecutivoHunter())
            .isFalse();
    }

    @Test
    public void isExecutivoOuExecutivoHunter_deveRetornarTrue_seUsuarioAutenticadoForCargoExecutivoOuExcutivoHunter() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.EXECUTIVO)
            .isExecutivoOuExecutivoHunter())
            .isTrue();

        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.EXECUTIVO_HUNTER)
            .isExecutivoOuExecutivoHunter())
            .isTrue();
    }

    @Test
    public void isExecutivoOuExecutivoHunter_deveRetornarFalse_seUsuarioAutenticadoNaoForExecutivoOuExcutivoHunter() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isExecutivoOuExecutivoHunter())
            .isFalse();
    }

    @Test
    public void isBackoffice_deveRetornarTrue_seUsuarioAutenticadoForNivelBackoffice() {
        assertThat(umUsuarioAutenticadoNivelBackoffice()
            .isBackoffice())
            .isTrue();
    }

    @Test
    public void isBackoffice_deveRetornarFalse_seUsuarioAutenticadoNaoForNivelBackoffice() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isBackoffice())
            .isFalse();
    }

    @Test
    public void isGerenteInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoForNivelOperacaoECargoInternetGerente() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_GERENTE)
            .isGerenteInternetOperacao())
            .isTrue();
    }

    @Test
    public void isGerenteInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoInternetGerente() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isGerenteInternetOperacao())
            .isFalse();
    }

    @Test
    public void isSupervisorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoForNivelOperacaoECargoInternetSupervisor() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_SUPERVISOR)
            .isSupervisorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isSupervisorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoInternetSupervisor() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isSupervisorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isCoordenadorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoForNivelOperacaoECargoInternetCoordenador() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_COORDENADOR)
            .isCoordenadorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isCoordenadorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoInternetCoordenador() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isCoordenadorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isVendedorInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoForNivelOperacaoECargoInternetVendedor() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_VENDEDOR)
            .isVendedorInternetOperacao())
            .isTrue();
    }

    @Test
    public void isVendedorInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoInternetVendedor() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isVendedorInternetOperacao())
            .isFalse();
    }

    @Test
    public void isBackofficeInternetOperacao_deveRetornarTrue_seUsuarioAutenticadoForNivelOperacaoECargoInternetBackoffice() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.INTERNET_BACKOFFICE)
            .isBackofficeInternetOperacao())
            .isTrue();
    }

    @Test
    public void isBackofficeInternetOperacao_deveRetornarFalse_seUsuarioAutenticadoNaoForCargoInternetBackoffice() {
        assertThat(umUsuarioAutenticadoCanalInternet(CodigoCargo.ASSISTENTE_OPERACAO)
            .isBackofficeInternetOperacao())
            .isFalse();
    }

    @Test
    public void haveCanalDoorToDoor_deveRetornarTrue_seUsuarioAutenticadoTerCanalD2dProprio() {
        var usuarioAutenticado = umUsuarioSuperiorD2d();
        usuarioAutenticado.setUsuario(umUsuario(null, null, Set.of(ECanal.D2D_PROPRIO)));
        assertThat(usuarioAutenticado
            .haveCanalDoorToDoor())
            .isTrue();
    }

    @Test
    public void haveCanalDoorToDoor_deveRetornarFalse_seUsuarioAutenticadoNaoTerCanalD2dProprio() {
        var usuarioAutenticado = umUsuarioSuperiorAtivoLocal();
        usuarioAutenticado.setUsuario(umUsuario(null, null, Set.of(ECanal.ATIVO_PROPRIO)));
        assertThat(usuarioAutenticado
            .haveCanalDoorToDoor())
            .isFalse();
    }
}
