package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponseTest;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaRealHistorico;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaRealPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRealHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRealRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static helpers.TestBuilders.umUsuarioAutenticado;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguracaoAgendaRealServiceTest {

    @InjectMocks
    private ConfiguracaoAgendaRealService service;
    @Mock
    private ConfiguracaoAgendaRealHistoricoRepository historicoRepository;
    @Mock
    private ConfiguracaoAgendaRealRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AgenteAutorizadoService aaService;
    @Mock
    private ConfiguracaoAgendaRealService self;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "self", self);
        when(repository.getQtdHorasPadrao()).thenReturn(24);
    }

    @Test
    public void salvar_deveSalvarConfiguracao_quandoDadosValidos() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado());
        when(repository.existsByCanal(any()))
            .thenReturn(false);

        assertThat(service.salvar(umaConfiguracaoAgendaRequest()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgendaResponse(), "dataCadastro");

        verify(historicoRepository).save((ConfiguracaoAgendaRealHistorico) MockitoHamcrest.argThat(
            hasProperty("acao", equalTo(EAcao.CADASTRO))));
        verify(repository).existsByCanal(ECanal.AGENTE_AUTORIZADO);
        verify(self).flushCacheConfigCanal();
    }

    @Test
    public void salvar_deveLancarException_quandoConfigExistente() {
        when(repository.existsByCanal(any()))
            .thenReturn(true);

        assertThatCode(() -> service.salvar(umaConfiguracaoAgendaRequest()))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não é possível salvar uma configuração já existente.");

        verify(repository).existsByCanal(ECanal.AGENTE_AUTORIZADO);
        verify(repository, never()).save(any(ConfiguracaoAgendaReal.class));
        verify(self, never()).flushCacheConfigCanal();
        verifyZeroInteractions(historicoRepository);
    }

    @Test
    public void salvar_deveLancarException_quandoNivelOperacao() {
        assertThatCode(() -> service.salvar(umaConfiguracaoAgendaRequest(CodigoNivel.OPERACAO)))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não é possível criar configurações para esse nível, "
                + "por favor selecione um canal ou subcanal.");

        verifyZeroInteractions(self, repository, historicoRepository, autenticacaoService);
    }

    @Test
    public void atualizar_deveAtualizarELimparCache_quandoSolicitado() {
        var configEsperada = umaConfiguracaoAgenda();
        configEsperada.setQtdHorasAdicionais(200);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado());
        when(repository.findById(100))
            .thenReturn(Optional.ofNullable(umaConfiguracaoAgenda()));

        service.atualizar(100, 200);

        verify(historicoRepository).save((ConfiguracaoAgendaRealHistorico) MockitoHamcrest.argThat(
            hasProperty("acao", equalTo(EAcao.ATUALIZACAO))));
        verify(repository).save(configEsperada);
        verify(self).flushCacheConfigCanal();
    }

    @Test
    public void findAll_deveListarConfiguracoes_quandoSolicitado() {
        var predicateEsperado = new ConfiguracaoAgendaRealPredicate()
            .comTipoConfiguracao(ETipoConfiguracao.CANAL)
            .comCanal(ECanal.AGENTE_AUTORIZADO)
            .build();

        when(repository.findAllByPredicate(any(), any()))
            .thenReturn(new PageImpl<>(singletonList(umaConfiguracaoAgenda())));

        assertThat(service.findAll(umaConfiguracaoAgendaFiltros(), new PageRequest()))
            .hasSize(1)
            .containsExactly(umaConfiguracaoAgendaResponse());

        verify(repository).findAllByPredicate(eq(predicateEsperado), any());
    }

    @Test
    public void alterarSituacao_deveAlterarSituacao_quandoSituacaoDiferente() {
        var configuracaoAlterada = umaConfiguracaoAgenda();
        configuracaoAlterada.setSituacao(ESituacao.I);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado());
        when(repository.findById(any())).thenReturn(Optional.of(umaConfiguracaoAgenda()));
        when(repository.save(configuracaoAlterada)).thenReturn(configuracaoAlterada);

        assertThatCode(() -> service.alterarSituacao(1, ESituacao.I))
            .doesNotThrowAnyException();

        verify(historicoRepository).save((ConfiguracaoAgendaRealHistorico) MockitoHamcrest.argThat(
            hasProperty("acao", equalTo(EAcao.INATIVACAO))));
        verify(repository).save(configuracaoAlterada);
        verify(self).flushCacheConfigCanal();
    }

    @Test
    public void alterarSituacao_deveLancarException_quandoSituacaoIgual() {
        when(repository.findById(any())).thenReturn(Optional.of(umaConfiguracaoAgenda()));

        assertThatCode(() -> service.alterarSituacao(1, ESituacao.A))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Configuração já possui a mesma situação.");

        verifyZeroInteractions(historicoRepository);
    }

    @Test
    public void alterarSituacao_deveLancarException_quandoSituacaoPadrao() {
        when(repository.findById(any())).thenReturn(Optional.of(umaConfiguracaoAgendaPadrao()));

        assertThatCode(() -> service.alterarSituacao(1, ESituacao.I))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não é possível alterar a situação da configuração padrão.");

        verifyZeroInteractions(historicoRepository);
    }

    @Test
    public void alterarSituacao_deveLancarException_quandoNaoAcharConfiguracao() {
        assertThatCode(() -> service.alterarSituacao(1, ESituacao.A))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Configuração de agenda não encontrada.");

        verifyZeroInteractions(historicoRepository);
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPeloCanal_quandoOperadorAaConsultar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.OPERACAO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.AGENTE_AUTORIZADO);
        when(repository.findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO))
            .thenReturn(Optional.of(16));

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null, 100))
            .isEqualTo(16);

        verify(repository, times(1)).findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO);
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
        verifyZeroInteractions(aaService);
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPeloSubcanal_quandoQualquerUsuarioD2dConsultar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.OPERACAO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.D2D_PROPRIO);
        when(repository.findQtdHorasAdicionaisBySubcanal(any()))
            .thenReturn(Optional.of(16));

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(ETipoCanal.PAP.getId(), null))
            .isEqualTo(16);

        verify(repository, times(1)).findQtdHorasAdicionaisBySubcanal(ETipoCanal.PAP.getId());
        verify(repository, never()).findQtdHorasAdicionaisByCanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
        verifyZeroInteractions(aaService);
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPelaEstrutura_quandoQualquerUsuarioAaConsultar() {
        var canal = ECanal.AGENTE_AUTORIZADO;
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.AGENTE_AUTORIZADO));
        when(autenticacaoService.getUsuarioCanal()).thenReturn(canal);
        when(repository.findQtdHorasAdicionaisByEstruturaAa(any()))
            .thenReturn(Optional.of(16));
        when(autenticacaoService.getTokenProperty(any(), any()))
            .thenReturn(Optional.of("AGENTE_AUTORIZADO"));

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null, 100))
            .isEqualTo(16);

        verify(repository).findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO");
        verify(repository, never()).findQtdHorasAdicionaisByCanal(any());
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
        verify(aaService, never()).getEstruturaByAgenteAutorizadoId(any());
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPelaEstrutura_quandoUsuarioSocioPrincipalConsultar() {
        var canal = ECanal.AGENTE_AUTORIZADO;
        var usuario = umUsuarioAutenticado(CodigoNivel.AGENTE_AUTORIZADO);
        usuario.setCargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO);
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        when(autenticacaoService.getUsuarioCanal()).thenReturn(canal);

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null, 100))
            .isEqualTo(24);

        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO");
        verify(repository, never()).findQtdHorasAdicionaisByCanal(any());
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository).findQtdHorasAdicionaisByNivel(any());
        verify(aaService).getEstruturaByAgenteAutorizadoId(any());
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveRetornarHorasPadrao_quandoNaoExistirConfig() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.OPERACAO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.AGENTE_AUTORIZADO);

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null, null))
            .isEqualTo(24);

        verify(repository, times(1)).findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO);
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
    }

    @Test
    public void findHistoricoByConfiguracaoId_deveListarHistorico_quandoSolicitado() {
        when(historicoRepository.findByConfiguracao_Id(eq(1), any()))
            .thenReturn(new PageImpl<>(List.of(umaConfiguracaoAgendaHistorico())));

        assertThat(service.findHistoricoByConfiguracaoId(1, new PageRequest()))
            .containsExactly(umaConfiguracaoAgendaHistoricoResponse())
            .hasSize(1);
    }
}
