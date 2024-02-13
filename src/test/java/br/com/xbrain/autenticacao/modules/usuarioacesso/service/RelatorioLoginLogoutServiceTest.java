package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.LoginLogoutHelper.umaListaLoginLogoutResponse;
import static helpers.MatchersHelper.anyOrNull;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class RelatorioLoginLogoutServiceTest {

    @InjectMocks
    private RelatorioLoginLogoutService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;
    @Mock
    private ParceirosOnlineService parceirosOnlineService;
    @Mock
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void getLoginsLogoutsDeHoje_permissaoException_quandoNaoTiverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(eq(101));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getLoginsLogoutsDeHoje(new PageRequest(), ECanal.D2D_PROPRIO, 101, null));
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveRetornarListaVazia_quandoNaoEncontrarLoginsLogouts() {
        when(notificacaoUsuarioAcessoService.buscarAcessosEntreDatasPorUsuarios(eq(new RelatorioLoginLogoutRequest())))
            .thenReturn(Collections.emptyList());

        assertThat(service.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest())).isEmpty();
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveRetornarListaPreenchida_quandoEncontrarLoginsLogouts() {
        when(notificacaoUsuarioAcessoService.buscarAcessosEntreDatasPorUsuarios(eq(new RelatorioLoginLogoutRequest())))
            .thenReturn(umaListaLoginLogoutResponse());

        assertThat(service.buscarAcessosEntreDatasPorUsuarios(new RelatorioLoginLogoutRequest()))
            .isEqualTo(umaListaLoginLogoutResponse());
    }

    @Test
    public void getCsv_permissaoException_quandoNaoTiverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(eq(101));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getCsv(
                new RelatorioLoginLogoutCsvFiltro(),
                new MockHttpServletResponse(),
                ECanal.D2D_PROPRIO, 101, null));
    }

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioAgenteAutorizadoBuscarInativos() {
        var usuarioAutenticado = umUsuarioAgenteAutorizado();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds(List.of(98, 100, 333, 2002, 15, 1, 9, 16));

        var predicateBuscaIds = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .build();
        when(usuarioRepository.findAllIds(eq(predicateBuscaIds)))
            .thenReturn(List.of(98, 100, 333, 2002, 15, 1, 9, 16));

        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(eq(Optional.of(List.of(98, 100, 333, 2002, 15, 1, 9, 16)))))
            .thenReturn(List.of(100, 2002, 1));

        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 2002, 1))
            .comSituacoes(Set.of(ESituacao.A, ESituacao.I, ESituacao.R))
            .build();
        when(usuarioRepository.findAllUsuariosNomeComSituacao(eq(predicate), eq(QUsuario.usuario.nome.upper().asc())))
            .thenReturn(List.of(
                UsuarioNomeResponse.of(100, "Hwasa Maria", ESituacao.A),
                UsuarioNomeResponse.of(2002, "Ary da Disney", ESituacao.A),
                UsuarioNomeResponse.of(1, "Adilson Elias", ESituacao.I)
            ));

        var colaboradores = service.getColaboradores(ECanal.D2D_PROPRIO, null, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicateBuscaIds));

        assertThat(colaboradores)
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(100, "Hwasa Maria", ESituacao.A),
                tuple(2002, "Ary da Disney", ESituacao.A),
                tuple(1, "Adilson Elias", ESituacao.I)
            );

        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioCoordenadorOperacaoBuscarInativos() {
        var usuarioAutenticado = umUsuarioCoordenadorOperacao();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds(List.of(98, 100, 333, 2002, 15, 1, 9, 16));

        var predicateBuscaIds = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .build();
        when(usuarioRepository.findAllIds(eq(predicateBuscaIds)))
            .thenReturn(List.of(98, 100, 333, 2002, 15, 1, 9, 16));

        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(eq(Optional.of(List.of(98, 100, 333, 2002, 15, 1, 9, 16)))))
            .thenReturn(List.of(100, 2002, 1));

        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 2002, 1))
            .comSituacoes(Set.of(ESituacao.A, ESituacao.I, ESituacao.R))
            .build();
        when(usuarioRepository.findAllUsuariosNomeComSituacao(eq(predicate), eq(QUsuario.usuario.nome.upper().asc())))
            .thenReturn(List.of(
                UsuarioNomeResponse.of(100, "Hwasa Maria", ESituacao.A),
                UsuarioNomeResponse.of(2002, "Ary da Disney", ESituacao.A),
                UsuarioNomeResponse.of(1, "Adilson Elias", ESituacao.I)
            ));

        var colaboradores = service.getColaboradores(ECanal.D2D_PROPRIO, null, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicateBuscaIds));

        assertThat(colaboradores)
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(100, "Hwasa Maria", ESituacao.A),
                tuple(2002, "Ary da Disney", ESituacao.A),
                tuple(1, "Adilson Elias", ESituacao.I)
            );

        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_deveAplicarPredicateNaoRetornarNenhumUsuario_quandoNaoHouverRegistroParaOsUsuariosPermitidos() {
        mockAutenticacao(umUsuarioXBrain());

        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(eq(Optional.empty())))
            .thenReturn(List.of());

        service.getColaboradores(ECanal.D2D_PROPRIO, null, null);

        var predicateArgCaptor = ArgumentCaptor.forClass(Predicate.class);
        verify(usuarioRepository, times(1)).findAllUsuariosNomeComSituacao(predicateArgCaptor.capture(), any());

        var expectedPredicate = new BooleanBuilder(QUsuario.usuario.id.isNull())
            .and(QUsuario.usuario.situacao.in(Set.of(ESituacao.A, ESituacao.I, ESituacao.R)));
        assertThat(predicateArgCaptor.getValue()).isEqualTo(expectedPredicate);

        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
    }

    @Test
    public void getColaboradores_permissaoException_quandoNaoTiverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(eq(101));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getColaboradores(ECanal.D2D_PROPRIO, 101, null));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAgenteAutorizado() {
        var usuarioAutenticado = umUsuarioAgenteAutorizado();

        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, times(1)).validarPermissaoSobreOAgenteAutorizado(eq(67));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAssistenteOperacaoEAaNull() {
        var usuarioAutenticado = umUsuarioAssistenteOperacao();

        mockAutenticacao(usuarioAutenticado);

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.AGENTE_AUTORIZADO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.AGENTE_AUTORIZADO)
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.AGENTE_AUTORIZADO, null, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, never()).validarPermissaoSobreOAgenteAutorizado(anyOrNull());
        verify(agenteAutorizadoService, never()).getUsuariosIdsByAaId(anyOrNull(), anyOrNull());
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoOperacao() {
        var usuarioAutenticado = umUsuarioExecutivoOperacao();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, times(1)).validarPermissaoSobreOAgenteAutorizado(eq(67));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoHunterOperacao() {
        var usuarioAutenticado = umUsuarioExecutivoHunterOperacao();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, times(1)).validarPermissaoSobreOAgenteAutorizado(eq(67));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioCoordenadorOperacao() {
        var usuarioAutenticado = umUsuarioCoordenadorOperacao();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, times(1)).validarPermissaoSobreOAgenteAutorizado(eq(67));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioGerenteOperacao() {
        var usuarioAutenticado = umUsuarioGerenteOperacao();
        mockAutenticacao(usuarioAutenticado);
        mockBuscarUsuariosPermitidosIds();

        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .comCanal(ECanal.D2D_PROPRIO)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .comIds(List.of(12, 7, 90, 1, 3, 100))
            .build();
        when(usuarioRepository.findAllIds(eq(predicate))).thenReturn(List.of(12, 7, 90, 1, 3, 100));

        var usuariosIds = service.getUsuariosIdsComNivelDeAcesso(ECanal.D2D_PROPRIO, 67, null);

        verify(usuarioRepository, times(1)).findAllIds(eq(predicate));

        assertThat(usuariosIds).isPresent();
        assertThat(usuariosIds.get())
            .containsExactlyInAnyOrder(12, 7, 90, 1, 3, 100);

        verify(autenticacaoService, times(1)).validarPermissaoSobreOAgenteAutorizado(eq(67));
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_permissaoException_quandoNaoHouverPermissaoSobreOAa() {
        mockAutenticacao(umUsuarioAgenteAutorizado());
        doThrow(new PermissaoException())
            .when(autenticacaoService).validarPermissaoSobreOAgenteAutorizado(eq(101));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.getUsuariosIdsComNivelDeAcesso(ECanal.AGENTE_AUTORIZADO, 101, null));
    }

    private void mockAutenticacao(UsuarioAutenticado usuarioAutenticado) {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);
    }

    private void mockBuscarUsuariosPermitidosIds(List<Integer> ids) {
        when(agenteAutorizadoService.getUsuariosIdsByAaId(eq(67), eq(true)))
            .thenReturn(ids);
    }

    private void mockBuscarUsuariosPermitidosIds() {
        mockBuscarUsuariosPermitidosIds(umUsuariosIdsList());
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(89)
            .nome("Hwasa Maria")
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .build();
    }

    private UsuarioAutenticado umUsuarioXBrain() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.XBRAIN.name());
        return usuario;
    }

    private UsuarioAutenticado umUsuarioMso() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.MSO.name());
        return usuario;
    }

    private UsuarioAutenticado umUsuarioAgenteAutorizado() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.AGENTE_AUTORIZADO.name());
        usuario.setAgentesAutorizados(List.of(67, 90));
        return usuario;
    }

    private UsuarioAutenticado umUsuarioAssistenteOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.ASSISTENTE_OPERACAO);
        return usuario;
    }

    private UsuarioAutenticado umUsuarioExecutivoOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.EXECUTIVO);
        return usuario;
    }

    private UsuarioAutenticado umUsuarioExecutivoHunterOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.EXECUTIVO_HUNTER);
        return usuario;
    }

    private UsuarioAutenticado umUsuarioCoordenadorOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.COORDENADOR_OPERACAO);
        return usuario;
    }

    private UsuarioAutenticado umUsuarioGerenteOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.GERENTE_OPERACAO);
        return usuario;
    }

    private List<Integer> umUsuariosIdsList() {
        return List.of(12, 7, 90, 1, 3, 100);
    }
}
