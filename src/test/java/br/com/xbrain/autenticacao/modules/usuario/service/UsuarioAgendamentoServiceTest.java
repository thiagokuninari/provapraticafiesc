package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.querydsl.core.types.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UsuarioAgendamentoServiceTest {

    @InjectMocks
    private UsuarioAgendamentoService service;
    @Mock
    private UsuarioRepository repository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private CargoService cargoService;
    @Mock
    private EquipeVendasService equipeVendasService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private EquipeVendasUsuarioService equipeVendasUsuarioService;

    @Before
    public void setup() {
        when(usuarioService.findPermissoesByUsuario(umVendedorTelevendas())).thenReturn(umaPermissaoDeVendaDiscadora());
        when(usuarioService.findPermissoesByUsuario(umVendedorD2d())).thenReturn(umaPermissaoDeVendaPresencial());
        when(usuarioService.findPermissoesByUsuario(umSocioPrincipal())).thenReturn(umaPermissaoDeVendaPresencial());
        when(usuarioService.findPermissoesByUsuario(umSupervisor())).thenReturn(umaPermissaoDeVendaPresencial());
        when(usuarioService.findPermissoesByUsuario(umSuperviorTelevendas())).thenReturn(umaPermissaoDeVendaClickToCall());
        when(usuarioService.findPermissoesByUsuario(umSupervisor2())).thenReturn(umaPermissaoDeVendaPresencial());
        when(usuarioService.findPermissoesByUsuario(umUsuarioAutenticadoSupervisor().getUsuario()))
            .thenReturn(umaPermissaoDeVendaClickToCall());
        when(agenteAutorizadoService.getUsuariosByAaId(1300, false))
            .thenReturn(UsuariosDoAa1300ComEquipesDeVendas());
        when(agenteAutorizadoService.getUsuariosByAaId(999, true))
            .thenReturn(listaUsuariosDoAgenteAutorizado999());
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarListaVazia_quandoNaoTiverVendedoresSupervisionadosDoSupervisorAutenticado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());
        when(equipeVendasService.getEquipesPorSupervisor(135)).thenReturn(List.of());

        assertThat(service.recuperarUsuariosParaDistribuicao(135, 1300, "PRESENCIAL"))
            .isEmpty();

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(7)).findPermissoesByUsuario(any(Usuario.class));
        verify(equipeVendasService).getEquipesPorSupervisor(135);
        verify(cargoService).findByUsuarioId(135);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarApenasUsuarioHibridosEVendedoresSupervisionados_quandoUsuarioAutenticadoSupervisor() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());
        when(equipeVendasService.getEquipesPorSupervisor(135)).thenReturn(List.of(umaEquipeDeVendas()));

        assertThat(service.recuperarUsuariosParaDistribuicao(135, 1300, "PRESENCIAL"))
            .hasSize(1)
            .extracting(
                UsuarioAgenteAutorizadoAgendamentoResponse::getId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getNome)
            .containsExactlyInAnyOrder(
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(7)).findPermissoesByUsuario(any(Usuario.class));
        verify(equipeVendasService).getEquipesPorSupervisor(135);
        verify(cargoService).findByUsuarioId(135);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarUsuariosHibridosEVendedoresLojas_quandoTipoContatoPresencial() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSocio());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());

        assertThat(service.recuperarUsuariosParaDistribuicao(100, 1300, "PRESENCIAL"))
            .hasSize(4)
            .extracting(
                UsuarioAgenteAutorizadoAgendamentoResponse::getId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getNome)
            .containsExactlyInAnyOrder(
                tuple(131, "JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR"),
                tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS"),
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"),
                tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(8)).findPermissoesByUsuario(any(Usuario.class));
        verifyNoMoreInteractions(equipeVendasService);
        verify(cargoService).findByUsuarioId(100);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarUsuariosHibridosEVendedoresTelevendas_quandoTipoContatoDiscadora() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSocio());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());

        assertThat(service.recuperarUsuariosParaDistribuicao(100, 1300, "DISCADORA"))
            .hasSize(4)
            .extracting(
                UsuarioAgenteAutorizadoAgendamentoResponse::getId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getNome)
            .containsExactlyInAnyOrder(
                tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"),
                tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS"),
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"),
                tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(8)).findPermissoesByUsuario(any(Usuario.class));
        verifyNoMoreInteractions(equipeVendasService);
        verify(cargoService).findByUsuarioId(100);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarUsuariosHibridosEVendedoresTelevendas_quandoTipoContatoClickToCall() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSocio());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());

        assertThat(service.recuperarUsuariosParaDistribuicao(100, 1300, "CLICK_TO_CALL"))
            .hasSize(4)
            .extracting(
                UsuarioAgenteAutorizadoAgendamentoResponse::getId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getNome)
            .containsExactlyInAnyOrder(
                tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS"),
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"),
                tuple(134, "MARIA DA SILVA SAURO SANTOS"),
                tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(8)).findPermissoesByUsuario(any(Usuario.class));
        verifyNoMoreInteractions(equipeVendasService);
        verify(cargoService).findByUsuarioId(100);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarApenasSocioPrincipal_quandoSupervisorSemPermissaoVendaESemVendedores() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSocio());
        when(usuarioService.findPermissoesByUsuario(any(Usuario.class))).thenReturn(umaPermissaoVisualizarGeral());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());

        assertThat(service.recuperarUsuariosParaDistribuicao(100, 1300, "DISCADORA"))
            .hasSize(1)
            .extracting(
                UsuarioAgenteAutorizadoAgendamentoResponse::getId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getNome
            )
            .containsExactlyInAnyOrder(
                tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(8)).findPermissoesByUsuario(any(Usuario.class));
        verifyNoMoreInteractions(equipeVendasService);
        verify(cargoService).findByUsuarioId(100);
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_naodeveRetornarVendedor_quandoForMesmoUsuarioDeDistribuicao() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSocio());
        when(repository.getUsuariosFilter(any(Predicate.class))).thenReturn(usuariosDoAgenteAutorizado1300());

        assertThat(service.recuperarUsuariosParaDistribuicao(130, 1300, "PRESENCIAL"))
            .hasSize(4)
            .extracting("id", "nome")
            .doesNotContain(tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"));

        verify(agenteAutorizadoService).getUsuariosByAaId(1300, false);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(2)).getUsuariosFilter(any(Predicate.class));
        verify(usuarioService, times(8)).findPermissoesByUsuario(any(Usuario.class));
        verifyNoMoreInteractions(equipeVendasService);
        verify(cargoService).findByUsuarioId(130);
    }

    @Test
    public void getUsuariosParaDistribuicaoByEquipeVendaId_deveRetornarlistaUsuarioDistribuicaoResponse_quandoSolicitado() {
        Map<String, Object> filtros = Map.of("equipeVendaId", 100, "ativo", true);

        when(equipeVendasUsuarioService.getAll(filtros)).thenReturn(umaListaUsuariosDaEquipeVenda());

        assertThat(service.getUsuariosParaDistribuicaoByEquipeVendaId(100))
            .hasSize(2)
            .extracting(
                UsuarioDistribuicaoResponse::getId,
                UsuarioDistribuicaoResponse::getNome
            )
            .containsExactlyInAnyOrder(
                tuple(1, "RENATO"),
                tuple(2, "JOAO")
            );

        verify(equipeVendasUsuarioService).getAll(filtros);
    }

    @Test
    public void getUsuariosParaDistribuicaoByEquipeVendaId_deveRetornarListaVazia_quandoNaoEncontrarEquipeVenda() {
        assertThat(service.getUsuariosParaDistribuicaoByEquipeVendaId(100))
            .isEmpty();

        verify(equipeVendasUsuarioService).getAll(Map.of("equipeVendaId", 100, "ativo", true));
    }

    @Test
    public void recuperarUsuariosDisponiveisParaDistribuicao_deveRetornarusuariosDaEquipeVenda_seForSupervisorSemPermissaoDeVenda() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(9991, 999);
        var usuarioIds = List.of(9991, 9992, 9993, 9994, 9995);

        when(usuarioService.getUsuariosAtivosByIds(usuarioIds)).thenReturn(List.of(9991));
        when(usuarioService.findPermissoesByUsuario(any(Usuario.class))).thenReturn(umaPermissaoVisualizarGeral());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        when(equipeVendasService.getEquipesPorSupervisor(135)).thenReturn(List.of(umaEquipeDeVendas()));
        when(equipeVendasService.getByUsuario(9991)).thenReturn(umaEquipeVendasDto());
        when(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of(9991))).thenReturn(resultMap);

        assertThat(service.recuperarUsuariosDisponiveisParaDistribuicao(999))
                .hasSize(1)
                .extracting(UsuarioAgendamentoResponse::getId, UsuarioAgendamentoResponse::getNome)
            .contains(
                tuple(9991, "USUARIO 1 DO AA 999")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(999, true);
        verify(usuarioService).getUsuariosAtivosByIds(usuarioIds);
        verify(equipeVendasService).getUsuarioEEquipeByUsuarioIds(List.of(9991));
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioService).findPermissoesByUsuario(any(Usuario.class));
        verify(equipeVendasService).getEquipesPorSupervisor(135);
    }

    @Test
    public void recuperarUsuariosDisponiveisParaDistribuicao_deveRetornarusuariosDaEquipeVendaAndSupervisor_quandoSupervisorComPermissaoDeVenda() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(9991, 999);
        var usuarioIds = List.of(9991, 9992, 9993, 9994, 9995);

        when(usuarioService.getUsuariosAtivosByIds(usuarioIds)).thenReturn(List.of(9991));
        when(usuarioService.findPermissoesByUsuario(any(Usuario.class)))
            .thenReturn(umaPermissaoDeVendaPresencial());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        when(equipeVendasService.getEquipesPorSupervisor(135)).thenReturn(List.of(umaEquipeDeVendas()));
        when(equipeVendasService.getByUsuario(9991)).thenReturn(umaEquipeVendasDto());
        when(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of(9991))).thenReturn(resultMap);

        assertThat(service.recuperarUsuariosDisponiveisParaDistribuicao(999))
                .hasSize(2)
                .extracting(UsuarioAgendamentoResponse::getId, UsuarioAgendamentoResponse::getNome)
            .contains(
                tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS"),
                tuple(9991, "USUARIO 1 DO AA 999")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(999, true);
        verify(usuarioService).getUsuariosAtivosByIds(usuarioIds);
        verify(equipeVendasService).getUsuarioEEquipeByUsuarioIds(List.of(9991));
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioService).findPermissoesByUsuario(any(Usuario.class));
        verify(equipeVendasService).getEquipesPorSupervisor(135);
    }

    @Test
    public void recuperarUsuariosDisponiveisParaDistribuicao_deveRetornarTodosUsuariosDoAA_sePossuirVisualizacaoGeral() {
        var usuarioIds = List.of(9991, 9992, 9993, 9994, 9995);

        when(usuarioService.getUsuariosAtivosByIds(usuarioIds)).thenReturn(usuarioIds);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoCoordenadorComercial());
        when(equipeVendasService.getByUsuario(9991)).thenReturn(umaEquipeVendasDto());
        when(repository.findById(9992)).thenReturn(Optional.of(umUsuarioId9992()));
        when(repository.findById(9993)).thenReturn(Optional.of(umUsuarioId9993()));
        when(repository.findById(9994)).thenReturn(Optional.of(umUsuarioId9994()));
        when(repository.findById(9995)).thenReturn(Optional.of(umUsuarioId9995()));

        assertThat(service.recuperarUsuariosDisponiveisParaDistribuicao(999))
                .hasSize(5)
            .extracting(
                UsuarioAgendamentoResponse::getId,
                UsuarioAgendamentoResponse::getNome
            )
            .containsExactlyInAnyOrder(
                tuple(9991, "USUARIO 1 DO AA 999"),
                tuple(9992, "USUARIO 2 DO AA 999"),
                tuple(9993, "USUARIO 3 DO AA 999"),
                tuple(9994, "USUARIO 4 DO AA 999"),
                tuple(9995, "USUARIO 5 DO AA 999")
            );

        verify(agenteAutorizadoService).getUsuariosByAaId(999, true);
        verify(usuarioService).getUsuariosAtivosByIds(usuarioIds);
        verify(equipeVendasService).getUsuarioEEquipeByUsuarioIds(usuarioIds);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioService, never()).findPermissoesByUsuario(any(Usuario.class));
        verify(equipeVendasService, never()).getEquipesPorSupervisor(anyInt());
    }

}
