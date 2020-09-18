package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioXBrainBuscarInativos() {
        mockAutenticacao(umUsuarioXBrain());

        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(eq(Optional.empty())))
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

        var colaboradores = service.getColaboradores();

        assertThat(colaboradores)
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(100, "Hwasa Maria", ESituacao.A),
                tuple(2002, "Ary da Disney", ESituacao.A),
                tuple(1, "Adilson Elias", ESituacao.I)
            );
    }

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioAgenteAutorizadoBuscarInativos() {
        mockAutenticacao(umUsuarioAgenteAutorizado());

        when(agenteAutorizadoService.getIdsUsuariosSubordinados(eq(true)))
            .thenReturn(Sets.newLinkedHashSet(98, 100, 333, 2002, 15, 1, 9, 16));

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

        var colaboradores = service.getColaboradores();

        assertThat(colaboradores)
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(100, "Hwasa Maria", ESituacao.A),
                tuple(2002, "Ary da Disney", ESituacao.A),
                tuple(1, "Adilson Elias", ESituacao.I)
            );
    }

    @Test
    public void getColaboradores_colaboradoresComIdENome_quandoUsuarioCoordenadorOperacaoBuscarInativos() {
        mockAutenticacao(umUsuarioCoordenadorOperacao());

        when(usuarioService.getIdDosUsuariosSubordinados(eq(89), eq(true)))
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

        var colaboradores = service.getColaboradores();

        assertThat(colaboradores)
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(100, "Hwasa Maria", ESituacao.A),
                tuple(2002, "Ary da Disney", ESituacao.A),
                tuple(1, "Adilson Elias", ESituacao.I)
            );
    }

    @Test
    public void getColaboradores_deveAplicarPredicateNaoRetornarNenhumUsuario_quandoNaoHouverRegistroParaOsUsuariosPermitidos() {
        mockAutenticacao(umUsuarioXBrain());

        when(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(eq(Optional.empty())))
            .thenReturn(List.of());

        service.getColaboradores();

        var predicateArgCaptor = ArgumentCaptor.forClass(Predicate.class);
        verify(usuarioRepository, times(1)).findAllUsuariosNomeComSituacao(predicateArgCaptor.capture(), any());

        var expectedPredicate = new BooleanBuilder(QUsuario.usuario.id.isNull())
            .and(QUsuario.usuario.situacao.in(Set.of(ESituacao.A, ESituacao.I, ESituacao.R)));
        assertThat(predicateArgCaptor.getValue()).isEqualTo(expectedPredicate);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_optionalEmpty_quandoUsuarioXBrain() {
        mockAutenticacao(umUsuarioXBrain());
        assertThat(service.getUsuariosIdsComNivelDeAcesso()).isNotPresent();
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_optionalEmpty_quandoUsuarioMso() {
        mockAutenticacao(umUsuarioMso());
        assertThat(service.getUsuariosIdsComNivelDeAcesso()).isNotPresent();
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAgenteAutorizado() {
        mockAutenticacao(umUsuarioAgenteAutorizado());
        mockBuscarIdsNoParceiros();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioAssistenteOperacao() {
        mockAutenticacao(umUsuarioAssistenteOperacao());
        mockBuscarIdsNoParceiros();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoOperacao() {
        mockAutenticacao(umUsuarioExecutivoOperacao());
        mockBuscarIdsNoParceiros();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoParceiros_quandoUsuarioExecutivoHunterOperacao() {
        mockAutenticacao(umUsuarioExecutivoHunterOperacao());
        mockBuscarIdsNoParceiros();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioCoordenadorOperacao() {
        mockAutenticacao(umUsuarioCoordenadorOperacao());
        mockBuscarIdsNoAutenticacao();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    @Test
    public void getUsuariosIdsComNivelDeAcesso_deveBuscarIdsNoAutenticacao_quandoUsuarioGerenteOperacao() {
        mockAutenticacao(umUsuarioGerenteOperacao());
        mockBuscarIdsNoAutenticacao();
        assertThat(service.getUsuariosIdsComNivelDeAcesso())
            .isPresent()
            .get().asList()
            .containsExactlyInAnyOrder(12, 7, 90);
    }

    private void mockAutenticacao(UsuarioAutenticado usuarioAutenticado) {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);
    }

    private void mockBuscarIdsNoParceiros() {
        when(agenteAutorizadoService.getIdsUsuariosSubordinados(eq(true)))
            .thenReturn(umUsuariosIdsSet());
    }

    private void mockBuscarIdsNoAutenticacao() {
        when(usuarioService.getIdDosUsuariosSubordinados(eq(umUsuarioAutenticado().getId()), eq(true)))
            .thenReturn(umUsuariosIdsList());
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(89)
            .nome("Hwasa Maria")
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

    private Stream<Integer> umUsuariosIdsStream() {
        return Stream.of(12, 7, 90);
    }

    private Set<Integer> umUsuariosIdsSet() {
        return umUsuariosIdsStream().collect(Collectors.toSet());
    }

    private List<Integer> umUsuariosIdsList() {
        return umUsuariosIdsStream().collect(Collectors.toList());
    }
}
