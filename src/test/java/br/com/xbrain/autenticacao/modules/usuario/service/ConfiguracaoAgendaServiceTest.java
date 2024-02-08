package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static helpers.TestBuilders.umUsuarioAutenticado;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguracaoAgendaServiceTest {

    @InjectMocks
    private ConfiguracaoAgendaService service;
    @Mock
    private ConfiguracaoAgendaRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AgenteAutorizadoNovoService aaService;

    @Test
    public void salvar_deveSalvarConfiguracao_quandoSolicitado() {
        assertThat(service.salvar(umaConfiguracaoAgendaRequest()))
            .isEqualTo(umaConfiguracaoAgendaResponse());

        verify(repository).save(umaConfiguracaoAgenda());
    }

    @Test
    public void findAll_deveListarConfiguracoes_quandoSolicitado() {
        when(repository.findAllByPredicate(any(), any()))
            .thenReturn(new PageImpl<>(singletonList(umaConfiguracaoAgenda())));

        assertThat(service.findAll(new ConfiguracaoAgendaFiltros(), new PageRequest()))
            .hasSize(1)
            .containsExactly(umaConfiguracaoAgendaResponse());
    }

    @Test
    public void alterarSituacao_deveAlterarSituacao_quandoSituacaoDiferente() {
        var configuracaoAlterada = umaConfiguracaoAgenda();
        configuracaoAlterada.setSituacao(ESituacao.I);

        when(repository.findById(any())).thenReturn(Optional.of(umaConfiguracaoAgenda()));
        when(repository.save(configuracaoAlterada)).thenReturn(configuracaoAlterada);

        assertThatCode(() -> service.alterarSituacao(1, ESituacao.I))
            .doesNotThrowAnyException();

        verify(repository).save(configuracaoAlterada);
    }

    @Test
    public void alterarSituacao_deveLancarException_quandoSituacaoIgual() {
        when(repository.findById(any())).thenReturn(Optional.of(umaConfiguracaoAgenda()));

        assertThatCode(() -> service.alterarSituacao(1, ESituacao.A))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Configuração já possui a mesma situação.");
    }

    @Test
    public void alterarSituacao_deveLancarException_quandoNaoAcharConfiguracao() {
        assertThatCode(() -> service.alterarSituacao(1, ESituacao.A))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Configuração de agenda não encontrada.");
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPeloCanal_quandoOperadorAaConsultar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.OPERACAO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.AGENTE_AUTORIZADO);
        when(repository.findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO))
            .thenReturn(Optional.of(16));

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null))
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

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(ETipoCanal.PAP))
            .isEqualTo(16);

        verify(repository, times(1)).findQtdHorasAdicionaisBySubcanal(ETipoCanal.PAP);
        verify(repository, never()).findQtdHorasAdicionaisByCanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
        verifyZeroInteractions(aaService);
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveConsultarPeloSubcanal_quandoQualquerUsuarioAaConsultar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.AGENTE_AUTORIZADO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.AGENTE_AUTORIZADO);
        when(repository.findQtdHorasAdicionaisByEstruturaAa(any()))
            .thenReturn(Optional.of(16));
        when(aaService.getEstruturaByUsuarioId(any()))
            .thenReturn("AGENTE_AUTORIZADO");

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null))
            .isEqualTo(16);

        verify(repository).findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO");
        verify(repository, never()).findQtdHorasAdicionaisByCanal(any());
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
    }

    @Test
    public void getQtdHorasAdicionaisAgendaByUsuario_deveRetornarHorasPadrao_quandoNaoExistirConfig() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(CodigoNivel.OPERACAO));
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.AGENTE_AUTORIZADO);

        assertThat(service.getQtdHorasAdicionaisAgendaByUsuario(null))
            .isEqualTo(24);

        verify(repository, times(1)).findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO);
        verify(repository, never()).findQtdHorasAdicionaisBySubcanal(any());
        verify(repository, never()).findQtdHorasAdicionaisByEstruturaAa(any());
        verify(repository, never()).findQtdHorasAdicionaisByNivel(any());
    }
}
