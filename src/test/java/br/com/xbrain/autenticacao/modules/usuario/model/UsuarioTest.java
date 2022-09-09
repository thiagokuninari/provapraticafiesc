package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.VENDEDOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.ATIVO_LOCAL_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsuarioTest {

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForVendedor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.VENDEDOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForSupervisor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.SUPERVISOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForAssistente() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.ASSISTENTE_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarFalse_quandoForGerenteCordenadorOuDiretor() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(1, CodigoCargo.DIRETOR_OPERACAO)
            .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(1, CodigoCargo.COORDENADOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForExecutivoVendas() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.OPERACAO_EXECUTIVO_VENDAS)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void permiteEditar_deveRetornarTrue_quandoForAdmin() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, CodigoNivel.XBRAIN, CodigoCargo.ADMINISTRADOR)));

    }

    @Test
    public void permiteEditar_deveRetornarFalse_quandoOUsuarioEditadoForOMesmoDoAutenticado() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, OPERACAO, CodigoCargo.GERENTE_OPERACAO)));
    }

    @Test
    public void permiteEditar_deveRetornarFalse_quandoOUsuarioAutenticadoEhDaEquipeDeVendasEOEditadoNaoForVendedor() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.SUPERVISOR_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, OPERACAO, CodigoCargo.SUPERVISOR_OPERACAO)));
    }

    @Test
    public void permiteEditar_deveRetornarTrue_quandoOUsuarioAutenticadoEhDaEquipeDeVendasEOEditadoNaoForVendedor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.VENDEDOR_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, OPERACAO, CodigoCargo.SUPERVISOR_OPERACAO)));
    }

    @Test
    public void hasLoginNetSales_deveRetornarFalse_seUsuarioPossuirLoginNetSalesNulo() {
        assertThat(umUsuarioComLoginNetSales(null).hasLoginNetSales()).isFalse();
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_deveNaoRetornarErro_quandoUsuarioTiverPermissaoDoCargoSobreOCanal() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
        usuario.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));

        assertThatCode(usuario::verificarPermissaoCargoSobreCanais).doesNotThrowAnyException();
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_validacaoException_quandoUsuarioTiverPermissaoDoCargoSobreOCanal() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.D2D_PROPRIO));
        usuario.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(usuario::verificarPermissaoCargoSobreCanais)
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_deveNaoRetornarErro_quandoUsuarioNaoTiverNenhumCanal() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
        usuario.setCanais(Set.of());

        assertThatCode(usuario::verificarPermissaoCargoSobreCanais).doesNotThrowAnyException();
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_deveNaoRetornarErro_quandoUsuarioTiverCanaisNull() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
        usuario.setCanais(null);

        assertThatCode(usuario::verificarPermissaoCargoSobreCanais).doesNotThrowAnyException();
    }

    @Test
    public void hasLoginNetSales_deveRetornarFalse_seUsuarioPossuirLoginNetSalesVazio() {
        assertThat(umUsuarioComLoginNetSales("").hasLoginNetSales()).isFalse();
    }

    @Test
    public void isCanalAtivoLocalRemovido_deveRetornarFalse_seNaoPossuirCanais() {
        assertThat(new Usuario().isCanalAtivoLocalRemovido(Set.of()))
            .isFalse();
    }

    @Test
    public void isCanalAtivoLocalRemovido_deveRetornarFalse_seCanaisNaoPossuirAtivoProprio() {
        assertThat(umUsuario(null, null, Set.of(ECanal.D2D_PROPRIO)).isCanalAtivoLocalRemovido(Set.of()))
            .isFalse();
    }

    @Test
    public void isCanalAtivoLocalRemovido_deveRetornarTrue_seCanaisPossuirAtivoProprioMasCanaisNovosForNull() {
        assertThat(umUsuario(null, null, Set.of(ECanal.ATIVO_PROPRIO)).isCanalAtivoLocalRemovido(null))
            .isTrue();
    }

    @Test
    public void isCanalAtivoLocalRemovido_deveRetornarTrue_seCanaisPossuirAtivoProprioMasCanaisNovosNao() {
        assertThat(umUsuario(null, null, Set.of(ECanal.ATIVO_PROPRIO)).isCanalAtivoLocalRemovido(Set.of(ECanal.D2D_PROPRIO)))
            .isTrue();
    }

    @Test
    public void isCanalAgenteAutorizadoRemovido_deveRetornarFalse_seNaoPossuirCanais() {
        assertThat(new Usuario()
            .isCanalAgenteAutorizadoRemovido(Set.of()))
            .isFalse();
    }

    @Test
    public void isCanalAgenteAutorizadoRemovido_deveRetornarFalse_seCanaisNaoPossuiAgenteAutorizado() {
        assertThat(umUsuario(null, null, Set.of(ECanal.D2D_PROPRIO))
            .isCanalAgenteAutorizadoRemovido(Set.of()))
            .isFalse();
    }

    @Test
    public void isCanalAgenteAutorizadoRemovido_deveRetornarTrue_seCanaisPossuirAgenteAutorizadoMasCanaisNovosForNull() {
        assertThat(umUsuario(null, null, Set.of(ECanal.AGENTE_AUTORIZADO))
            .isCanalAgenteAutorizadoRemovido(null))
            .isTrue();
    }

    @Test
    public void isCanalAgenteAutorizadoRemovido_deveRetornarTrue_seCanaisPossuirAgenteAutorizadoMasCanaisNovosNao() {
        assertThat(umUsuario(null, null, Set.of(ECanal.AGENTE_AUTORIZADO))
            .isCanalAgenteAutorizadoRemovido(Set.of(ECanal.D2D_PROPRIO)))
            .isTrue();
    }

    @Test
    public void isNivelOperacao_deveRetornarTrue_seUsuarioPossuirNivelOperacao() {
        assertThat(usuarioAtivo(VENDEDOR_OPERACAO, OPERACAO).isNivelOperacao())
            .isTrue();
    }

    @Test
    public void isNivelOperacao_deveRetornarFalse_seUsuarioNaoPossuirNivelOperacao() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isNivelOperacao())
            .isFalse();
    }

    @Test
    public void isNivelOperacao_deveRetornarTrue_seUsuarioNaoPossuirCargoEForNivelOperacao() {
        assertThat(usuarioAtivo(null, OPERACAO).isNivelOperacao())
            .isTrue();
    }

    private static Cargo umCargo(CodigoCargo codigoCargo) {
        return Cargo
            .builder()
            .codigo(codigoCargo)
            .build();
    }

    private static Usuario umUsuarioComCargo(CodigoCargo codigoCargo) {
        return Usuario
            .builder()
            .cargo(umCargo(codigoCargo))
            .build();
    }

    private static Usuario umUsuarioComCargo(Integer id, CodigoCargo codigoCargo) {
        return Usuario.builder()
            .id(id)
            .cargo(Cargo
                .builder()
                .codigo(codigoCargo)
                .build())
            .build();
    }

    private static Usuario umUsuarioComLoginNetSales(String loginNetSales) {
        return Usuario
            .builder()
            .loginNetSales(loginNetSales)
            .build();
    }

    private static Usuario usuarioAtivo(CodigoCargo codigoCargo, CodigoNivel nivel) {
        var usuarioAtivo = Usuario
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .situacao(ESituacao.A)
                    .build())
                .build());
        return usuarioAtivo.build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoNivel codigoNivel, CodigoCargo codigoCargo) {
        return UsuarioAutenticado
            .builder()
            .id(id)
            .nivelCodigo(codigoNivel.name())
            .usuario(umUsuarioComCargo(codigoCargo))
            .build();
    }

    @Test
    public void hasLoginNetSales_deveRetornarTrue_seUsuarioPossuirLoginNetSales() {
        assertThat(umUsuarioComLoginNetSales("login").hasLoginNetSales()).isTrue();
    }

}
