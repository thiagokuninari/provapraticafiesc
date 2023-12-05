package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
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
        var usuarioAutenticado = umUsuarioAutenticado(2, OPERACAO, COORDENADOR_OPERACAO);
        usuarioAutenticado.setCargoCodigo(COORDENADOR_OPERACAO);
        var usuario = umUsuarioComCargo(1, VENDEDOR_OPERACAO);

        assertTrue(usuario.permiteEditar(usuarioAutenticado));
    }

    @Test
    public void permiteEditar_deveRetornarFalse_quandoUsuarioAutenticadoForSupervisor() {
        var usuarioAutenticado = umUsuarioAutenticado(1, OPERACAO, CodigoCargo.SUPERVISOR_OPERACAO);
        usuarioAutenticado.setCargoCodigo(CodigoCargo.SUPERVISOR_OPERACAO);
        var usuario = umUsuarioComCargo(1, VENDEDOR_OPERACAO);

        assertFalse(usuario.permiteEditar(usuarioAutenticado));
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

    @Test
    public void hasSubCanalPapPremium_deveRetornarTrue_seUsuarioPossuirSubCanalPapPremium() {
        assertTrue(umUsuarioOperacaoComSubCanal(101112, 3, PAP_PREMIUM).hasSubCanalPapPremium());
    }

    @Test
    public void hasSubCanalPapPremium_deveRetornarFalse_seUsuarioNaoPossuirSubCanalPapPremium() {
        assertFalse(umUsuarioOperacaoComSubCanal(101112, 1, PAP).hasSubCanalPapPremium());
    }

    @Test
    public void hasSubCanalInsideSalesPme_deveRetornarTrue_seUsuarioPossuirSubCanalInsideSalesPme() {
        assertTrue(umUsuarioOperacaoComSubCanal(101112, 4, INSIDE_SALES_PME).hasSubCanalInsideSalesPme());
    }

    @Test
    public void hasSubCanalInsideSalesPme_deveRetornarFalse_seUsuarioNaoPossuirSubCanalInsideSalesPme() {
        assertFalse(umUsuarioOperacaoComSubCanal(101112, 3, PAP_PREMIUM).hasSubCanalInsideSalesPme());
    }

    @Test
    public void hasLoginNetSales_deveRetornarTrue_seUsuarioPossuirLoginNetSales() {
        assertThat(umUsuarioComLoginNetSales("login").hasLoginNetSales()).isTrue();
    }

    @Test
    public void hasHierarquia_deveRetornarTrue_seUsuarioPossuirHierarquia() {
        var usuario = Usuario.builder()
            .hierarquiasId(List.of(10, 20))
            .build();
        assertTrue(usuario.hasHierarquia());
    }

    @Test
    public void hasHierarquia_deveRetornarFalse_seUsuarioNaoPossuirHierarquia() {
        assertFalse(new Usuario().hasHierarquia());
    }

    @Test
    public void hasSubCanaisDaHierarquia_deveRetornarTrue_seUsuarioPossuirSubCanaisDaHierarquia() {
        var usuario = Usuario.builder()
            .subCanais(Set.of(umSubCanal()))
            .build();
        assertTrue(usuario.hasSubCanaisDaHierarquia(Set.of(1, 2, 3, 4)));
    }

    @Test
    public void hasSubCanaisDaHierarquia_deveRetornarFalse_seUsuarioNaoPossuirSubCanaisDaHierarquia() {
        var usuario = Usuario.builder()
            .subCanais(Set.of(umSubCanal()))
            .build();
        assertFalse(usuario.hasSubCanaisDaHierarquia(Set.of(2, 3, 4)));
    }

    @Test
    public void hasAllSubCanaisDosSubordinados_deveRetornarTrue_seUsuarioSuperiorPossuirTodosSubCanaisDosSubordinados() {
        var usuario = Usuario.builder()
            .subCanais(Set.of(umSubCanal(), doisSubCanal()))
            .build();

        assertTrue(usuario.hasAllSubCanaisDosSubordinados(List.of(
            umUsuarioSubCanalId(10, "USUARIO 10", PAP.getId()),
            umUsuarioSubCanalId(20, "USUARIO 20", PAP_PME.getId())
        )));
    }

    @Test
    public void hasAllSubCanaisDosSubordinados_deveRetornarFalse_seUsuarioSuperiorNaoPossuirTodosSubCanaisDosSubordinados() {
        var usuario = Usuario.builder()
            .subCanais(Set.of(umSubCanal()))
            .build();

        assertFalse(usuario.hasAllSubCanaisDosSubordinados(List.of(
            umUsuarioSubCanalId(10, "USUARIO 10", PAP.getId()),
            umUsuarioSubCanalId(20, "USUARIO 20", PAP_PME.getId()),
            umUsuarioSubCanalId(30, "USUARIO 30", PAP_PREMIUM.getId()),
            umUsuarioSubCanalId(40, "USUARIO 40", INSIDE_SALES_PME.getId()),
            umUsuarioSubCanalId(50, "USUARIO 50", PAP_CONDOMINIO.getId())
        )));
    }

    @Test
    public void isGeradorLeadsOuClienteLojaFuturo_deveRetornarBoolean_seUsuarioGeradorLeadsOuLojaFuturo() {
        assertThat(umUsuarioComCargo(AGENTE_AUTORIZADO_VENDEDOR_D2D).isGeradorLeadsOuClienteLojaFuturo())
            .isFalse();

        assertThat(umUsuarioComCargo(CodigoCargo.GERADOR_LEADS).isGeradorLeadsOuClienteLojaFuturo())
            .isTrue();

        assertThat(umUsuarioComCargo(CLIENTE_LOJA_FUTURO).isGeradorLeadsOuClienteLojaFuturo())
            .isTrue();
    }

    @Test
    public void isNivelVarejo_deveRetornarTrue_seUsuarioPossuirNivelVarejo() {
        assertThat(usuarioAtivo(VAREJO_VENDEDOR, VAREJO).isNivelVarejo())
            .isTrue();
    }

    @Test
    public void isNivelVarejo_deveRetornarFalse_seUsuarioNaoPossuirNivelVarejo() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isNivelOperacao())
            .isFalse();
    }

    @Test
    public void isNivelReceptivo_deveRetornarTrue_seUsuarioPossuirNivelReceptivo() {
        assertThat(usuarioAtivo(VENDEDOR_RECEPTIVO, RECEPTIVO).isNivelReceptivo())
            .isTrue();
    }

    @Test
    public void isNivelReceptivo_deveRetornarFalse_seUsuarioNaoPossuirNivelReceptivo() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isNivelOperacao())
            .isFalse();
    }

    @Test
    public void isSupervisorOperacao_deveRetornarTrue_seUsuarioForSupervisorOperacao() {
        assertThat(usuarioAtivo(SUPERVISOR_OPERACAO, ATIVO_LOCAL_PROPRIO).isSupervisorOperacao()).isTrue();
    }

    @Test
    public void isSupervisorOperacao_deveRetornarFalse_seUsuarioNaoForSupervisorOperacao() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isSupervisorOperacao()).isFalse();
    }

    @Test
    public void isAssistenteOperacao_deveRetornarTrue_seUsuarioForSupervisorOperacao() {
        assertThat(usuarioAtivo(ASSISTENTE_OPERACAO, ATIVO_LOCAL_PROPRIO).isAssistenteOperacao()).isTrue();
    }

    @Test
    public void isAssistenteOperacao_deveRetornarFalse_seUsuarioNaoForSupervisorOperacao() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isAssistenteOperacao()).isFalse();
    }

    @Test
    public void isCargoAgenteAutorizado_deveRetornarTrue_seUsuarioForCargoAgenteAutorizado() {
        assertThat(usuarioAtivo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS, AGENTE_AUTORIZADO).isCargoAgenteAutorizado()).isTrue();
    }

    @Test
    public void isCargoAgenteAutorizado_deveRetornarFalse_seUsuarioNaoForCargoAgenteAutorizado() {
        assertThat(usuarioAtivo(OPERACAO_TELEVENDAS, ATIVO_LOCAL_PROPRIO).isCargoAgenteAutorizado()).isFalse();
    }

    @Test
    public void isCargoLojaFuturo_deveRetornarTrue_seUsuarioForClienteLojaFuturo() {
        assertThat(usuarioAtivo(CLIENTE_LOJA_FUTURO, AGENTE_AUTORIZADO).isCargoLojaFuturo()).isTrue();
    }

    @Test
    public void isCargoLojaFuturo_deveRetornarTrue_seUsuarioForAssistenteRelacionamento() {
        assertThat(usuarioAtivo(ASSISTENTE_RELACIONAMENTO, AGENTE_AUTORIZADO).isCargoLojaFuturo()).isTrue();
    }

    @Test
    public void isCargoLojaFuturo_deveRetornarFalse_seUsuarioNaoForCargoLojaFuturo() {
        assertThat(usuarioAtivo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS, AGENTE_AUTORIZADO).isCargoLojaFuturo()).isFalse();
    }

    @Test
    public void isCargoImportadorCargas_deveRetornarTrue_seUsuarioForCargoLojaFuturo() {
        assertThat(usuarioAtivo(IMPORTADOR_CARGAS, FEEDER).isCargoImportadorCargas()).isTrue();
    }

    @Test
    public void isCargoImportadorCargas_deveRetornarFalse_seUsuarioNaoForCargoLojaFuturo() {
        assertThat(usuarioAtivo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS, FEEDER).isCargoImportadorCargas()).isFalse();
    }

    @Test
    public void removerCaracteresDoCpf_deveRemoverFormatacao_quandoChamado() {
        var usuario = umUsuarioConvertFrom();
        usuario.setCpf("123.123.123-12");

        usuario.removerCaracteresDoCpf();

        assertThat(usuario.getCpf()).isEqualTo("12312312312");
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
}
