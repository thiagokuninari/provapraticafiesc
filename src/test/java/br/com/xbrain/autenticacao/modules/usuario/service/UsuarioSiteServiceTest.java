package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import helpers.TestBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioSiteServiceTest {

    @InjectMocks
    private UsuarioSiteService service;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private EquipeVendaD2dService equipeVendasD2dService;

    @Test
    public void getUsuariosDaHierarquiaDoUsuarioLogado_naoDeveRetornarUsuarios_seNaoEncontrado() {
        when(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .thenReturn(Collections.emptyList());

        assertThat(service.getUsuariosDaHierarquiaDoUsuarioLogado())
            .isEmpty();
    }

    @Test
    public void getUsuariosDaHierarquiaDoUsuarioLogado_deveRetornarUsuarios_seEncontrado() {
        when(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .thenReturn(List.of(
                TestBuilders.umUsuario(1, CodigoCargo.GERENTE_OPERACAO),
                TestBuilders.umUsuario(2, CodigoCargo.SUPERVISOR_OPERACAO)));

        assertThat(service.getUsuariosDaHierarquiaDoUsuarioLogado())
            .isEqualTo(List.of(
                TestBuilders.umUsuario(1, CodigoCargo.GERENTE_OPERACAO),
                TestBuilders.umUsuario(2, CodigoCargo.SUPERVISOR_OPERACAO)));
    }

    @Test
    public void getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado_naoDeveRetornarVendedores_seNaoEncontrado() {
        var usuario = TestBuilders.umUsuario(100, CodigoCargo.ADMINISTRADOR);
        usuario.getCargo().getNivel().setCodigo(CodigoNivel.XBRAIN);

        when(autenticacaoService.getUsuarioId()).thenReturn(100);
        when(usuarioRepository.findById(eq(100)))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.findVendedoresPorSiteId(eq(123)))
            .thenReturn(Collections.emptyList());

        assertThat(service.getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado(123, false))
            .isEqualTo(Collections.emptyList());
    }

    @Test
    public void getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado_deveRetornarVendedores_seEncontrado() {
        var usuario = TestBuilders.umUsuario(100, CodigoCargo.ADMINISTRADOR);
        usuario.getCargo().getNivel().setCodigo(CodigoNivel.XBRAIN);

        when(autenticacaoService.getUsuarioId()).thenReturn(100);
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.umUsuarioAutenticadoAdmin(100));
        when(usuarioRepository.findById(eq(100)))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.findVendedoresPorSiteId(eq(123)))
            .thenReturn(List.of(
                TestBuilders.umUsuarioNomeResponse(1, "VENDEDOR 1", ESituacao.A),
                TestBuilders.umUsuarioNomeResponse(2, "VENDEDOR 2", ESituacao.A)));

        assertThat(service.getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado(123, false))
            .isEqualTo(List.of(
                SelectResponse.of(1, "VENDEDOR 1"),
                SelectResponse.of(2, "VENDEDOR 2")));
    }
}
