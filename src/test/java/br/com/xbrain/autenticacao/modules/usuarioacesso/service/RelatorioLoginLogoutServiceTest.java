package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.LoginLogoutHelper.*;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.*;
import static helpers.MatchersHelper.anyOrNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class RelatorioLoginLogoutServiceTest {

    @InjectMocks
    private RelatorioLoginLogoutService service;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @Mock
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Test
    public void getLoginsLogoutsDeHoje_deveLancarPermissaoException_quandoNaoTiverPermissaoSobreOAa() {
        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getLoginsLogoutsDeHoje(new PageRequest(), ECanal.D2D_PROPRIO,
                101, null));

        verifyNoMoreInteractions(usuarioService);
        verifyNoMoreInteractions(usuarioRepository);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);
        verifyNoMoreInteractions(agenteAutorizadoNovoService);
        verifyNoMoreInteractions(notificacaoUsuarioAcessoService);
    }

    @Test
    public void getLoginsLogoutsDeHoje_retornarLoginsELogounts_quandoDadosValidos() {
        mockAutenticacao(umUsuarioAgenteAutorizado());
        when(agenteAutorizadoNovoService.getUsuariosIdsByAaId(101, true)).thenReturn(List.of(101));
        when(usuarioRepository.findAllIds(any())).thenReturn(List.of(89));
        when(notificacaoUsuarioAcessoService.getLoginsLogoutsDeHoje(Optional.of(List.of(89)), new PageRequest()))
            .thenReturn(umMongoosePageLoginLogoutResponse());

        assertThat(service.getLoginsLogoutsDeHoje(new PageRequest(), ECanal.D2D_PROPRIO,
            101, null)).isEqualTo(umaPaginaLoginLogoutResponse());

        verify(usuarioRepository).findAllIds(any());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(agenteAutorizadoNovoService).getUsuariosIdsByAaId(101, true);
        verify(notificacaoUsuarioAcessoService).getLoginsLogoutsDeHoje(Optional.of(List.of(89)), new PageRequest());
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveRetornarListaVazia_quandoNaoEncontrarLoginsLogouts() {
        when(notificacaoUsuarioAcessoService.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest()))
            .thenReturn(Collections.emptyList());

        assertThat(service.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest())).isEmpty();

        verify(notificacaoUsuarioAcessoService).buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest());
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveRetornarListaPreenchida_quandoEncontrarLoginsLogouts() {
        when(notificacaoUsuarioAcessoService.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest()))
            .thenReturn(umaListaLoginLogoutResponse());

        assertThat(service.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest()))
            .isEqualTo(umaListaLoginLogoutResponse());

        verify(notificacaoUsuarioAcessoService).buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest());
    }

    @Test
    public void getCsv_permissaoException_quandoNaoTiverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getCsv(
                new RelatorioLoginLogoutCsvFiltro(),
                new MockHttpServletResponse(),
                ECanal.D2D_PROPRIO, 101, null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);
    }

    @Test
    public void getCsv_deveBuscarCsv_quandoDadosValidos() {
        mockAutenticacao(umUsuarioAgenteAutorizado());
        when(agenteAutorizadoNovoService.getUsuariosIdsByAaId(101, true)).thenReturn(List.of(101));
        when(usuarioRepository.findAllIds(any())).thenReturn(List.of(89));

        service.getCsv(
            new RelatorioLoginLogoutCsvFiltro(),
            new MockHttpServletResponse(),
            ECanal.D2D_PROPRIO, 101, null);

        verify(usuarioRepository).findAllIds(any());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(notificacaoUsuarioAcessoService).getCsv(any(), any());
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);
    }

    @Test
    public void getColaboradores_deveRetornarColaboradoresComIdENome_quandoUsuarioAgenteAutorizadoBuscarInativos() {
        var usuarioAutenticado = umUsuarioAgenteAutorizado();
        var predicateBuscaIds = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds(List.of(98, 100, 333, 2002, 15, 1, 9, 16));
        when(usuarioRepository.findAllIds(predicateBuscaIds))
            .thenReturn(List.of(98, 100, 333, 2002, 15, 1, 9, 16));
        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(Optional.of(List.of(98, 100, 333, 2002, 15, 1, 9, 16))))
            .thenReturn(List.of(100, 2002, 1));

        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 2002, 1))
            .comSituacoes(Set.of(ESituacao.A, ESituacao.I, ESituacao.R))
            .build();
        var lista = List.of(
            UsuarioNomeResponse.of(100, "Hwasa Maria", ESituacao.A),
            UsuarioNomeResponse.of(2002, "Ary da Disney", ESituacao.A),
            UsuarioNomeResponse.of(1, "Adilson Elias", ESituacao.I)
        );

        when(usuarioRepository.findAllUsuariosNomeComSituacao(predicate, QUsuario.usuario.nome.upper().asc()))
            .thenReturn(lista);

        assertThat(service.getColaboradores(ECanal.D2D_PROPRIO, null, null)).isEqualTo(lista);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicateBuscaIds);
        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioCoordenadorOperacaoBuscarInativos() {
        var usuarioAutenticado = umUsuarioCoordenadorOperacao();
        var predicateBuscaIds = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .build();
        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 2002, 1))
            .comSituacoes(Set.of(ESituacao.A, ESituacao.I, ESituacao.R))
            .build();
        var lista = List.of(
            UsuarioNomeResponse.of(100, "Hwasa Maria", ESituacao.A),
            UsuarioNomeResponse.of(2002, "Ary da Disney", ESituacao.A),
            UsuarioNomeResponse.of(1, "Adilson Elias", ESituacao.I)
        );

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds(List.of(98, 100, 333, 2002, 15, 1, 9, 16));
        when(usuarioRepository.findAllIds(predicateBuscaIds)).thenReturn(List.of(98, 100, 333, 2002, 15, 1, 9, 16));
        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(Optional.of(List.of(98, 100, 333, 2002, 15, 1, 9, 16))))
            .thenReturn(List.of(100, 2002, 1));
        when(usuarioRepository.findAllUsuariosNomeComSituacao(predicate, QUsuario.usuario.nome.upper().asc()))
            .thenReturn(lista);

        assertThat(service.getColaboradores(ECanal.D2D_PROPRIO, null, null))
            .isEqualTo(lista);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicateBuscaIds);
        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_deveAplicarPredicateNaoRetornarNenhumUsuario_quandoNaoHouverRegistroParaOsUsuariosPermitidos() {
        var predicateArgCaptor = ArgumentCaptor.forClass(Predicate.class);

        mockAutenticacao(umUsuarioXBrain());

        assertThat(service.getColaboradores(ECanal.D2D_PROPRIO, null, null))
            .isEqualTo(List.of());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllUsuariosNomeComSituacao(predicateArgCaptor.capture(), any());
        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_permissaoException_quandoNaoTiverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getColaboradores(ECanal.D2D_PROPRIO, 101, null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAgenteAutorizado() {
        var usuarioAutenticado = umUsuarioAgenteAutorizado();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(67);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAssistenteOperacaoEAaNull() {
        var usuarioAutenticado = umUsuarioAssistenteOperacao();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.AGENTE_AUTORIZADO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.AGENTE_AUTORIZADO)
            .build();

        mockAutenticacao(usuarioAutenticado);
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.AGENTE_AUTORIZADO, null, null);
        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
        verify(agenteAutorizadoNovoService, never()).getUsuariosIdsByAaId(anyOrNull(), anyOrNull());
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoOperacao() {
        var usuarioAutenticado = umUsuarioExecutivoOperacao();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);
        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(67);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoHunterOperacao() {
        var usuarioAutenticado = umUsuarioExecutivoHunterOperacao();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);
        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(67);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioCoordenadorOperacao() {
        var usuarioAutenticado = umUsuarioCoordenadorOperacao();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);
        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(67);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioGerenteOperacao() {
        var usuarioAutenticado = umUsuarioGerenteOperacao();
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();
        when(usuarioRepository.findAllIds(predicate)).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);
        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioRepository).findAllIds(predicate);
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(67);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_permissaoException_quandoNaoHouverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());
        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getUsuariosIdsComNivelDeAcesso(ECanal.AGENTE_AUTORIZADO, 101,
                null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(101);
    }

    private void mockAutenticacao(UsuarioAutenticado usuarioAutenticado) {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);
    }

    private void mockBuscarUsuariosPermitidosIds(List<Integer> ids) {
        when(agenteAutorizadoNovoService.getUsuariosIdsByAaId(67, true))
            .thenReturn(ids);
    }

    private void mockBuscarUsuariosPermitidosIds() {
        mockBuscarUsuariosPermitidosIds(umUsuariosIdsList());
    }
}
