package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.ATIVO_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;

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
        usuario.getCargo().setCanais(Set.of(ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
        usuario.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));

        assertThatCode(usuario::verificarPermissaoCargoSobreCanais).doesNotThrowAnyException();
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_validacaoException_quandoUsuarioTiverPermissaoDoCargoSobreOCanal() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ATIVO_PROPRIO, ECanal.D2D_PROPRIO));
        usuario.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(usuario::verificarPermissaoCargoSobreCanais)
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_deveNaoRetornarErro_quandoUsuarioNaoTiverNenhumCanal() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
        usuario.setCanais(Set.of());

        assertThatCode(usuario::verificarPermissaoCargoSobreCanais).doesNotThrowAnyException();
    }

    @Test
    public void verificarPermissaoCargoSobreCanais_deveNaoRetornarErro_quandoUsuarioTiverCanaisNull() {
        var usuario = umUsuarioComCargo(26, CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO);
        usuario.getCargo().setCanais(Set.of(ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO));
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
        assertThat(umUsuario(null, null, Set.of(ATIVO_PROPRIO)).isCanalAtivoLocalRemovido(null))
            .isTrue();
    }

    @Test
    public void isCanalAtivoLocalRemovido_deveRetornarTrue_seCanaisPossuirAtivoProprioMasCanaisNovosNao() {
        assertThat(umUsuario(null, null, Set.of(ATIVO_PROPRIO)).isCanalAtivoLocalRemovido(Set.of(ECanal.D2D_PROPRIO)))
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

    @Test
    public void getTerritorioMercadoDesenvolvimentoIdOrNull_deveRetornarMercadoDesenId_quandoIdMercadoDesenvolvimentoPresente() {
        var usuario = new Usuario();
        usuario.setTerritorioMercadoDesenvolvimentoId(12345);

        Integer esperado = 12345;
        var resultado = usuario.getTerritorioMercadoDesenvolvimentoIdOrNull();

        assertEquals(esperado, resultado);
    }

    @Test
    public void getTerritorioMercadoDesenvolvimentoIdOrNull_deveRetornarNull_quandoIdMercadoDesenvolvimentoNull() {
        var usuarioDto = new Usuario();
        usuarioDto.setTerritorioMercadoDesenvolvimentoId(null);

        var result = usuarioDto.getTerritorioMercadoDesenvolvimentoIdOrNull();

        assertEquals(null, result);
    }

    @Test
    public void from_deveRetonarUsuario_quandoReceberUsuarioDadosAcessoRequest() {
        assertThat(Usuario.from(umUsuarioDadosAcessoRequest()))
            .extracting("id", "email")
            .containsExactly(2245, "email2245@xbrain.com");
    }

    @Test
    public void forceLoadCanais_deveRetornarUsuarioComCanaisCarregados_quandoUsuarioPossuiCanais() {
        assertThat(umUsuarioComCanais().forceLoadCanais())
            .extracting("id")
            .containsExactly(1);

    }

    @Test
    public void configurarRamal_deveSetarRamal_quandoConfiguracaoNaoNull() {
        var configuracao = new Configuracao();
        configuracao.setId(1);
        configuracao.setRamal(8989);
        var usuario = Usuario
            .builder()
            .configuracao(configuracao)
            .id(1)
            .build();

        assertThat(usuario.getConfiguracao().getRamal())
            .isEqualTo(8989);

        usuario.configurarRamal(3333);

        assertThat(usuario.getConfiguracao().getRamal())
            .isEqualTo(3333);
    }

    @Test
    public void tratarEmails_deveTransformarEmUpperCaseERetirarOsEspacosDoEmail_quandoEmailNaoNull() {
        var usuario = Usuario.builder()
                .id(1)
                .nome("Admin")
                .email("     usuario@xbrain.com.br     ")
                .build();
        assertThat(usuario.getEmail()).isEqualTo("     usuario@xbrain.com.br     ");

        usuario.tratarEmails();

        assertThat(usuario.getEmail()).isEqualTo("USUARIO@XBRAIN.COM.BR");
    }

    @Test
    public void tratarEmails_deveTransformarEmUpperCaseERetirarOsEspacosEmailEEmail02_quandoEmailEEmail02NaoNull() {
        var usuario = Usuario.builder()
            .id(1)
            .nome("Admin")
            .email("     usuario@xbrain.com.br     ")
            .email02("     email02@xbrain.com.br      ")
            .build();
        assertThat(usuario.getEmail()).isEqualTo("     usuario@xbrain.com.br     ");
        assertThat(usuario.getEmail02()).isEqualTo("     email02@xbrain.com.br      ");

        usuario.tratarEmails();

        assertThat(usuario.getEmail()).isEqualTo("USUARIO@XBRAIN.COM.BR");
        assertThat(usuario.getEmail02()).isEqualTo("EMAIL02@XBRAIN.COM.BR");
    }

    @Test
    public void getCargoId_deveRetornarCargoId_quandoCargoNaoNull() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.getCargo().setId(9090);

        assertThat(usuario.getCargoId())
            .isEqualTo(9090);
    }

    @Test
    public void getCargoId_deveRetornarNull_quandoCargoNull() {
        var usuario = Usuario.builder()
            .id(1)
            .nome("Admin")
            .empresas(List.of(new Empresa(1, "nome")))
            .build();

        assertThat(usuario.getCargoId())
            .isNull();
    }

    @Test
    public void getCargosSuperioresId_deveRetornarCargoId_quandoCargoNaoNull() {
        var cargo = umCargo(EXECUTIVO);
        cargo.setId(8787);
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.getCargo().setSuperiores(Set.of(cargo));

        assertThat(usuario.getCargosSuperioresId())
            .isEqualTo(Set.of(8787));
    }

    @Test
    public void getCargosSuperioresId_deveRetornarNull_quandoCargoNull() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.setCargo(null);

        assertThat(usuario.getCargosSuperioresId())
            .isNull();
    }

    @Test
    public void getCodigoCargoByCanais_deveRetornarOperacaoTelevendas_quandoCanalForAtivoProprio() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.setCanais(Set.of(ATIVO_PROPRIO));

        assertThat(usuario.getCodigoCargoByCanais())
            .isEqualTo(Set.of(OPERACAO_TELEVENDAS));
    }

    @Test
    public void getCodigoCargoByCanais_deveRetornarOperacaoTelevendasEVendedorOperacao_quandoCanalForMaiorQueUm() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.setCanais(Set.of(ATIVO_PROPRIO, ECanal.VAREJO, ECanal.INTERNET));

        assertThat(usuario.getCodigoCargoByCanais())
            .isEqualTo(Set.of(OPERACAO_TELEVENDAS, VENDEDOR_OPERACAO));
    }

    @Test
    public void getCodigoCargoByCanais_deveRetornarVendedorOperacao_quandoCanalForIgualAUmENaoForAtivoProprio() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.setCanais(Set.of(ECanal.VAREJO));

        assertThat(usuario.getCodigoCargoByCanais())
            .isEqualTo(Set.of(VENDEDOR_OPERACAO));
    }

    @Test
    public void getCodigoCargoByCanais_deveRetornarVendedorOperacao_quandoCanalForNull() {
        var usuario = umUsuarioComCargo(23, EXECUTIVO);
        usuario.setCanais(null);

        assertThat(usuario.getCodigoCargoByCanais())
            .isEqualTo(Set.of(VENDEDOR_OPERACAO));
    }

    @Test
    public void getNivelId_deveRetornarNivelId_quandoCargoENivelNaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(OPERACAO).build());

        assertThat(usuario.getNivelId())
            .isEqualTo(3434);
    }

    @Test
    public void getNivelId_deveRetornarNull_quandoCargoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setCargo(null);

        assertThat(usuario.getNivelId())
            .isNull();
    }

    @Test
    public void getNivelId_deveRetornarNull_quandoNivelNaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.getCargo().setNivel(null);

        assertThat(usuario.getNivelId())
            .isNull();
    }

    @Test
    public void isEmpty_deveRetornarTrue_quandoIdENomeECpfEEmailNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setId(null);
        usuario.setNome(null);
        usuario.setCpf(null);
        usuario.setEmail(null);

        assertThat(usuario.isEmpty())
            .isTrue();
    }

    @Test
    public void isEmpty_deveRetornarFalse_quandoIdNaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setId(23);
        usuario.setNome(null);
        usuario.setCpf(null);
        usuario.setEmail(null);

        assertThat(usuario.isEmpty())
            .isFalse();
    }

    @Test
    public void hasUsuarioCadastro_deveRetornarTrue_quandoUsuarioCadastroNaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setUsuarioCadastro(umUsuarioCadastro());

        assertThat(usuario.hasUsuarioCadastro())
            .isTrue();
    }

    @Test
    public void hasUsuarioCadastro_deveRetornarFalse_quandoUsuarioCadastroNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setUsuarioCadastro(null);

        assertThat(usuario.hasUsuarioCadastro())
            .isFalse();
    }

    @Test
    public void hasConfiguracao_deveRetornarTrue_quandoConfiguracaoNaoNull() {
        var config = new Configuracao();
        config.setId(2);
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setConfiguracao(config);

        assertThat(usuario.hasConfiguracao())
            .isTrue();
    }

    @Test
    public void hasConfiguracao_deveRetornarFalse_quandoConfiguracaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setConfiguracao(null);

        assertThat(usuario.hasConfiguracao())
            .isFalse();
    }

    @Test
    public void getTipoFeedersString_deveRetornarListaTipoFeeder_quandoTipoFeederNaoNull() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.setConfiguracao(null);
        usuario.setTiposFeeder(Set.of(ETipoFeederMso.EMPRESARIAL, ETipoFeederMso.RESIDENCIAL));

        assertThat(usuario.getTipoFeedersString())
            .isEqualTo(Set.of("EMPRESARIAL", "RESIDENCIAL"));
    }

    @Test
    public void isAgenteAutorizado_deveRetornarTrue_quandoCodigoNivelForAgenteAutorizado() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(AGENTE_AUTORIZADO).build());

        assertThat(usuario.isAgenteAutorizado())
            .isTrue();
    }

    @Test
    public void isAgenteAutorizado_deveRetornarFalse_quandoCodigoNivelNaoForAgenteAutorizado() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(XBRAIN).build());

        assertThat(usuario.isAgenteAutorizado())
            .isFalse();
    }

    @Test
    public void isSocioPrincipal_deveRetornarTrue_quandoCodigoNivelForSocioPrincipal() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);

        assertThat(usuario.isSocioPrincipal())
            .isTrue();
    }

    @Test
    public void isSocioPrincipal_deveRetornarFalse_quandoCodigoNivelNaoForSocioPrincipal() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);

        assertThat(usuario.isSocioPrincipal())
            .isFalse();
    }

    @Test
    public void isBackoffice_deveRetornarTrue_quandoCodigoNivelForBackoffice() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(BACKOFFICE).build());

        assertThat(usuario.isSocioPrincipal())
            .isTrue();
    }

    @Test
    public void isBackoffice_deveRetornarFalse_quandoCodigoNivelNaoForBackoffice() {
        var usuario = umUsuarioComCargo(23, OPERACAO_TELEVENDAS);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(XBRAIN).build());

        assertThat(usuario.isSocioPrincipal())
            .isFalse();
    }

    @Test
    public void convertFrom_deveRetornarListaIds_quandoInformadoUsuarios() {
        assertThat(Usuario.convertFrom(Set.of(
            umUsuarioComCargo(23, OPERACAO_TELEVENDAS),
            umUsuarioComCargo(24, OPERACAO_TELEVENDAS),
            umUsuarioComCargo(25, OPERACAO_TELEVENDAS)
        ))).isEqualTo(Set.of(23, 24, 25));
    }

    @Test
    public void isCoordenadorOuSupervisorOperacao_deveRetornarTrue_quandoCargoCodigoForCoordenadorOperacao() {
        var usuario = umUsuarioComCargo(23, COORDENADOR_OPERACAO);

        assertThat(usuario.isCoordenadorOuSupervisorOperacao())
            .isTrue();
    }

    @Test
    public void isCoordenadorOuSupervisorOperacao_deveRetornarTrue_quandoCargoCodigoForSupervisorOperacao() {
        var usuario = umUsuarioComCargo(23, SUPERVISOR_OPERACAO);

        assertThat(usuario.isCoordenadorOuSupervisorOperacao())
            .isTrue();
    }

    @Test
    public void isCoordenadorOuSupervisorOperacao_deveRetornarFalse_quandoCargoNaoForSupervisorECoordenadorDeOperacao() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);

        assertThat(usuario.isCoordenadorOuSupervisorOperacao())
            .isFalse();
    }

    @Test
    public void isNivelVarejo_deveRetornarTrue_quandoNivelCargoForVarejo() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(VAREJO).build());
        assertThat(usuario.isNivelVarejo())
            .isTrue();
    }

    @Test
    public void isNivelVarejo_deveRetornarFalse_quandoNivelCargoNaoForVarejo() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);
        usuario.getCargo().setNivel(Nivel.builder().id(3434).codigo(AGENTE_AUTORIZADO).build());
        assertThat(usuario.isNivelVarejo())
            .isFalse();
    }

    @Test
    public void numeroTentativasLoginSenhaIncorreta_deveRetornarQuantidadeHistoricoSenhaIncorreta_quandoSolicitado() {
        var usuario = umUsuarioComCargo(23, AGENTE_AUTORIZADO_SOCIO);
        var senhaIncorreta = UsuarioSenhaIncorretaHistorico.builder().id(1).build();
        usuario.setHistoricosSenhaIncorretas(List.of(senhaIncorreta, senhaIncorreta, senhaIncorreta));

        assertThat(usuario.numeroTentativasLoginSenhaIncorreta())
            .isEqualTo(3);
    }

    @Test
    public void isTecnico_deveRetornarTrue_quandoForCargoTecnico() {
        assertThat(umUsuarioComCargo(AGENTE_AUTORIZADO_TECNICO_GERENTE).isTecnico()).isTrue();
    }

    @Test
    public void isTecnico_deveRetornarFalse_quandoNaoForCargoTecnico() {
        assertThat(umUsuarioComCargo(VENDEDOR_OPERACAO).isTecnico()).isFalse();
    }

    @Test
    public void isTecnico_deveRetornarFalse_quandoCargoForNull() {
        assertThat(new Usuario().isTecnico()).isFalse();
    }

    @Test
    public void isTecnico_deveRetornarFalse_quandoCodigoCargoForNull() {
        var usuario = Usuario
            .builder()
            .cargo(umCargo(null))
            .build();
        assertThat(usuario.isTecnico()).isFalse();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarFalse_quandoUsuarioPossuirDadosNetSales() {
        assertThat(umUsuarioComDadosNetSales().hasNotDadosNetSales()).isFalse();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirLoginNetSalesNull() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setLoginNetSales(null);
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirLoginNetSalesEmpty() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setLoginNetSales("");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirLoginNetSalesBlank() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setLoginNetSales(" ");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCanalNetSalesCodigoNull() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCanalNetSalesCodigo(null);
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCanalNetSalesCodigoEmpty() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCanalNetSalesCodigo("");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCanalNetSalesCodigoBlank() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCanalNetSalesCodigo(" ");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarFalse_quandoUsuarioPossuirLoginNetSalesEmpty() {
        var usuario = umUsuarioComDadosNetSales();
        assertThat(usuario.hasNotDadosNetSales()).isFalse();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCanalNetSalesIdNull() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCanalNetSalesId(null);
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirNomeEquipeVendaNetSalesNull() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setNomeEquipeVendaNetSales(null);
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirNomeEquipeVendaNetSalesEmpty() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setNomeEquipeVendaNetSales("");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirNomeEquipeVendaNetSalesBlank() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setNomeEquipeVendaNetSales(" ");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCodigoEquipeVendaNetSalesNull() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCodigoEquipeVendaNetSales(null);
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCodigoEquipeVendaNetSalesEmpty() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCodigoEquipeVendaNetSales("");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void hasNotDadosNetSales_deveRetornarTrue_quandoUsuarioPossuirCodigoEquipeVendaNetSalesBlank() {
        var usuario = umUsuarioComDadosNetSales();
        usuario.setCodigoEquipeVendaNetSales(" ");
        assertThat(usuario.hasNotDadosNetSales()).isTrue();
    }

    @Test
    public void isSocioPrincipalOuAceite_deveRetornarTrue_quandoUsuarioForNivelAgenteAutorizadoECargoSocioPrincipal() {
        assertThat(umUsuarioComCargoENivel(AGENTE_AUTORIZADO, AGENTE_AUTORIZADO_SOCIO).isSocioPrincipalOuAceite())
            .isTrue();
    }

    @Test
    public void isSocioPrincipalOuAceite_deveRetornarTrue_quandoUsuarioForNivelAgenteAutorizadoECargoAceite() {
        assertThat(umUsuarioComCargoENivel(AGENTE_AUTORIZADO, AGENTE_AUTORIZADO_ACEITE).isSocioPrincipalOuAceite())
            .isTrue();
    }

    @Test
    public void isSocioPrincipalOuAceite_deveRetornarFalse_quandoUsuarioNaoForNivelAgenteAutorizadoECargoAceite() {
        assertThat(umUsuarioComCargoENivel(OPERACAO, AGENTE_AUTORIZADO_ACEITE).isSocioPrincipalOuAceite())
            .isFalse();
    }

    @Test
    public void isSocioPrincipalOuAceite_deveRetornarFalse_quandoUsuarioForNivelAgenteAutorizadoENaoForCargoAceiteOuSocio() {
        assertThat(umUsuarioComCargoENivel(AGENTE_AUTORIZADO, VENDEDOR_OPERACAO).isSocioPrincipalOuAceite())
            .isFalse();
    }

    @Test
    public void isOperadorTelevendasAtivoLocal_deveRetornarTrue_quandoCargoForOperacaoTelevendasECanalAtivoProprio() {
        var usuario = Usuario
            .builder()
            .cargo(umCargo(OPERACAO_TELEVENDAS))
            .canais(Set.of(ATIVO_PROPRIO))
            .build();
        assertThat(usuario.isOperadorTelevendasAtivoLocal()).isTrue();
    }

    @Test
    public void isOperadorTelevendasAtivoLocal_deveRetornarFalse_quandoCargoNaoForOperacaoTelevendasECanalAtivoProprio() {
        var usuario = Usuario
            .builder()
            .cargo(umCargo(AGENTE_AUTORIZADO_ACEITE))
            .canais(Set.of(ATIVO_PROPRIO))
            .build();
        assertThat(usuario.isOperadorTelevendasAtivoLocal()).isFalse();
    }

    @Test
    public void isOperadorTelevendasAtivoLocal_deveRetornarFalse_quandoCargoNaoOperacaoTelevendasENaoForCanalAtivoProprio() {
        var usuario = Usuario
            .builder()
            .cargo(umCargo(OPERACAO_TELEVENDAS))
            .canais(Set.of(D2D_PROPRIO))
            .build();
        assertThat(usuario.isOperadorTelevendasAtivoLocal()).isFalse();
    }

    private static Usuario umUsuarioComCargoENivel(CodigoNivel nivelCodigo, CodigoCargo codigoCargo) {
        return Usuario
            .builder()
            .cargo(Cargo.builder()
                .codigo(codigoCargo)
                .nivel(Nivel.builder()
                    .codigo(nivelCodigo)
                    .build())
                .build())
            .build();
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

    private static Usuario umUsuarioComCanais() {
        return Usuario
            .builder()
            .id(1)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoNivel codigoNivel, CodigoCargo codigoCargo) {
        return UsuarioAutenticado
            .builder()
            .id(id)
            .nivelCodigo(codigoNivel.name())
            .usuario(umUsuarioComCargo(codigoCargo))
            .build();
    }

    private UsuarioDadosAcessoRequest umUsuarioDadosAcessoRequest() {
        return UsuarioDadosAcessoRequest.builder()
            .usuarioId(2245)
            .emailNovo("email2245@xbrain.com")
            .build();
    }
}
