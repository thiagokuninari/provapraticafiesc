package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.permissao.helpers.PermissaoEspecialHelper.umDtoNovoSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.permissao.helpers.PermissaoEspecialHelper.umaListaPermissoesEspeciaisFuncFeederEAcompIndTecVend;
import static br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService.FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissaoEspecialServiceTest {

    @InjectMocks
    private PermissaoEspecialService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private PermissaoEspecialRepository repository;
    @Mock
    private ColaboradorVendasService colaboradorVendasService;
    @Mock
    private FeederService feederService;
    @Captor
    private ArgumentCaptor<PermissaoEspecial> permissaoEspecialCaptor;

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveProcessarPermissoes_seIdNull() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        service.processarPermissoesEspeciaisGerentesCoordenadores(null);

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(feederService, times(1)).salvarPermissoesEspeciaisCoordenadoresGerentes(eq(List.of()), eq(1));
    }

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveProcessarPermissoes_sePassarIdPorParametro() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(colaboradorVendasService.getUsuariosAaFeederPorCargo(anyList(), anyList())).thenReturn(List.of(1, 2));

        service.processarPermissoesEspeciaisGerentesCoordenadores(List.of(1));

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(colaboradorVendasService, times(1)).getUsuariosAaFeederPorCargo(List.of(1), umaListaCodigoCargo());
        verify(feederService, times(1)).salvarPermissoesEspeciaisCoordenadoresGerentes(eq(List.of(1, 2)), eq(1));
    }

    @Test
    public void save_deveChamarRepository_quandoReceberRequest() {

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        var request = PermissaoEspecialRequest.builder()
            .usuarioId(2424)
            .funcionalidadesIds(List.of(3, 7))
            .build();

        service.save(request);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(repository).save(captor.capture());

        var permissoesSalvas = captor.getValue();
        assertThat(permissoesSalvas).hasSize(2);
        assertThat(permissoesSalvas.get(0))
            .extracting("funcionalidade.id", "usuario.id")
            .containsExactly(3, 2424);

        assertThat(permissoesSalvas.get(1))
            .extracting("funcionalidade.id", "usuario.id")
            .containsExactly(7, 2424);

    }

    @Test
    public void save_deveChamarRepository_quandoReceberLista() {
        var listaPermissoes = List.of(
            new PermissaoEspecial()
        );
        service.save(listaPermissoes);
        verify(repository).save(listaPermissoes);
    }

    @Test
    public void hasPermissaoEspecialAtiva_deveRetornarTrue_seUsuarioPossuirPermissaoEspecialAtiva() {
        when(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000)))
            .thenReturn(true);

        assertThat(service.hasPermissaoEspecialAtiva(1, 1000))
            .isTrue();

        verify(repository).existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000));
    }

    @Test
    public void hasPermissaoEspecialAtiva_deveRetornarFalse_seUsuarioNaoPossuirPermissaoEspecialAtiva() {
        when(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000)))
            .thenReturn(false);

        assertThat(service.hasPermissaoEspecialAtiva(1, 1000))
            .isFalse();

        verify(repository).existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000));
    }

    @Test
    public void remover_deveSetarUsuarioBaixaEDataBaixa_quandoRequisicaoBemSucedida() {
        var permissao = new PermissaoEspecial();
        when(autenticacaoService.getUsuarioId()).thenReturn(9090);
        when(repository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(1, 2))
            .thenReturn(Optional.of(permissao));
        when(repository.save(any(PermissaoEspecial.class))).thenReturn(permissao);

        assertThat(permissao.getDataBaixa()).isNull();
        assertThat(permissao.getUsuarioBaixa()).isNull();

        service.remover(1, 2);

        assertThat(permissao.getDataBaixa()).isNotNull();
        assertThat(permissao.getUsuarioBaixa().getId()).isEqualTo(9090);

        verify(autenticacaoService).getUsuarioId();
        verify(repository).findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(1, 2);
        verify(repository).save(any(PermissaoEspecial.class));
    }

    @Test
    public void remover_deveLancarException_quandoPermissaoNaoEncontrada() {
        when(repository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(1, 2))
            .thenReturn(Optional.empty());

        assertThatCode(() -> service.remover(1, 2))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Permissão Especial não encontrada.");

        verify(autenticacaoService, never()).getUsuarioId();
        verify(repository).findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(1, 2);
        verify(repository, never()).save(any(PermissaoEspecial.class));
    }

    @Test
    public void deletarPermissoesEspeciaisBy_deveRemoverPermissoesDosUsuariosEChamarRepository_quandoChamado() {
        var funcionalidadesIds = List.of(1000, 2000);
        var usuariosIds = List.of(1, 2, 3);

        service.deletarPermissoesEspeciaisBy(funcionalidadesIds, usuariosIds);

        verify(repository).deletarPermissaoEspecialBy(eq(funcionalidadesIds), eq(usuariosIds));
    }

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_naoDeveProcessarPermissoes_seUsuarioAutenticadoNaoForXbrain() {
        var usuarioAutenticadoOperacao = umUsuarioAutenticado();
        usuarioAutenticadoOperacao.setNivelCodigo(CodigoNivel.OPERACAO.name());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticadoOperacao);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.processarPermissoesEspeciaisGerentesCoordenadores(List.of(1)))
            .withMessageContaining("Usuário não autorizado!");

        verify(colaboradorVendasService, never()).getUsuariosAaFeederPorCargo(any(), any());
        verify(feederService, never()).salvarPermissoesEspeciaisCoordenadoresGerentes(any(), anyInt());
    }

    @Test
    public void reprocessarPermissoesEspeciaisSociosSecundarios_naoDeveReprocessarPermissoes_quandoUsuarioNaoForXbrain() {
        var usuarioAutenticado = umUsuarioAutenticado();
        usuarioAutenticado.setNivelCodigo(CodigoNivel.OPERACAO.name());

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.reprocessarPermissoesEspeciaisSociosSecundarios(List.of()))
            .withMessage("Usuário não autorizado!");

        verify(autenticacaoService).getUsuarioAutenticado();
        verifyZeroInteractions(colaboradorVendasService);
        verifyZeroInteractions(feederService);
    }

    @Test
    public void reprocessarPermissoesEspeciaisSociosSecundarios_naoDeveReprocessarPermissoes_quandoErroComClient() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        doThrow(RetryableException.class)
            .when(colaboradorVendasService)
            .getUsuariosAaFeederPorCargo(List.of(), List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO));

        assertThatExceptionOfType(RetryableException.class)
            .isThrownBy(() -> service.reprocessarPermissoesEspeciaisSociosSecundarios(List.of()));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(colaboradorVendasService).getUsuariosAaFeederPorCargo(List.of(),
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO));
        verifyZeroInteractions(feederService);
    }

    @Test
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveReprocessarPermissoes_quandoListaIdNull() {
        var usuarioAutenticado = umUsuarioAutenticado();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);
        when(colaboradorVendasService.getUsuariosAaFeederPorCargo(null,
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO))).thenReturn(List.of(123));

        service.reprocessarPermissoesEspeciaisSociosSecundarios(null);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(colaboradorVendasService).getUsuariosAaFeederPorCargo(null,
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO));
        verify(feederService).salvarPermissoesEspeciaisSociosSecundarios(List.of(123), usuarioAutenticado.getId());
    }

    @Test
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveReprocessarPermissoes_quandoAaIdPreenchido() {
        var usuarioAutenticado = umUsuarioAutenticado();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);
        when(colaboradorVendasService.getUsuariosAaFeederPorCargo(List.of(1),
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO))).thenReturn(List.of(123));

        service.reprocessarPermissoesEspeciaisSociosSecundarios(List.of(1));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(colaboradorVendasService).getUsuariosAaFeederPorCargo(List.of(1),
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO));
        verify(feederService).salvarPermissoesEspeciaisSociosSecundarios(List.of(123), usuarioAutenticado.getId());
    }

    @Test
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveReprocessarPermissoes_quandoListaIdVazia() {
        var usuarioAutenticado = umUsuarioAutenticado();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);
        when(colaboradorVendasService.getUsuariosAaFeederPorCargo(List.of(),
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO))).thenReturn(List.of(123));

        service.reprocessarPermissoesEspeciaisSociosSecundarios(List.of());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(colaboradorVendasService).getUsuariosAaFeederPorCargo(List.of(),
            List.of(AGENTE_AUTORIZADO_SOCIO_SECUNDARIO));
        verify(feederService).salvarPermissoesEspeciaisSociosSecundarios(List.of(123), usuarioAutenticado.getId());
    }

    @Test
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_deveAtualizarAsPermissoesDoNovoSocio_quandoSolicitado() {
        doReturn(umaListaPermissoesEspeciaisFuncFeederEAcompIndTecVend())
            .when(repository)
            .findAllByFuncionalidadeIdInAndUsuarioIdAndDataBaixaIsNull(FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR, 32);

        assertThatCode(() -> service
            .atualizarPermissoesEspeciaisNovoSocioPrincipal(umDtoNovoSocioPrincipal(32)))
            .doesNotThrowAnyException();

        verify(repository)
            .findAllByFuncionalidadeIdInAndUsuarioIdAndDataBaixaIsNull(FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR, 32);
        verify(repository, times(5))
            .save(permissaoEspecialCaptor.capture());

        assertThat(permissaoEspecialCaptor.getAllValues())
            .extracting("funcionalidade.id")
            .containsExactlyInAnyOrder(3046, 15000, 15005, 15012, 16101);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_naoDeveAtualizarAsPermissoesDoSocio_seAntigosSociosPrincipaisForVazio() {
        assertThatCode(() -> service
            .atualizarPermissoesEspeciaisNovoSocioPrincipal(umDtoNovoSocioPrincipal()))
            .doesNotThrowAnyException();

        verifyZeroInteractions(repository);
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .build();
    }

    private List<CodigoCargo> umaListaCodigoCargo() {
        return List.of(AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_COORDENADOR);
    }
}
