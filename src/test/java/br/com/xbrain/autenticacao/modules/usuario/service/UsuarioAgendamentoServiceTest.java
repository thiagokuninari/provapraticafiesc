package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({UsuarioAgendamentoService.class})
public class UsuarioAgendamentoServiceTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private CargoService cargoService;
    @MockBean
    private EquipeVendasService equipeVendasService;
    @MockBean
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @Autowired
    private UsuarioAgendamentoService usuarioAgendamentoService;

    @Before
    public void setup() {
        when(usuarioRepository.findById(eq(9991))).thenReturn(umUsuarioId9991());
        when(agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(eq(1300), any()))
                .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1300());
        when(agenteAutorizadoNovoService.getUsuariosByAaId(eq(1300), eq(false)))
                .thenReturn(todosUsuariosDoAgenteAutorizado1300());
        when(agenteAutorizadoNovoService.getUsuariosByAaId(eq(999), anyBoolean()))
                .thenReturn(todosUsuariosDoAgenteAutorizado999());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(133).build())))
                .thenReturn(umaPermissaoDeVendaResponse());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(135).build())))
                .thenReturn(umaPermissaoResponseVazia());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(cargoService.findByUsuarioId(eq(130))).thenReturn(umCargoVendedorTelevendas());
        when(cargoService.findByUsuarioId(eq(132))).thenReturn(umCargoSocioPrincipal());
        when(cargoService.findByUsuarioId(eq(150))).thenReturn(umCargoVendedorTelevendas());
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveTrazerSupervisor_seForOUsuarioLogado() {
        when(usuarioRepository.getUsuariosFilter(any())).thenReturn(usuariosDoAgenteAutorizado1300());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(135).build())))
                .thenReturn(umaPermissaoDeVendaResponse());

        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .contains(135, "MARCOS AUGUSTO DA SILVA SANTOS");
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_naoDeveMostrarSupervisor_seNaoPossuirPermissaoDeVenda() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS"));
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_naoDeveMostrarVendedores_seUsuarioForDeCargoHibrido() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(132, 1300);

        assertThat(usuariosParaDistribuicao)
                .extracting("id", "nome")
                .containsOnly(tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_naoDeveMostrarVendedor_seForDeOutroSegmento() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(131, "JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_naoDeveMostrarVendedor_seForMesmoUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"));
    }

    @Test
    public void recuperarUsuariosParaDistribuicao_deveRetornarUsuariosSupervisionadosAndSupervisor_seForSupervisor() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoSupervisor());
        when(equipeVendasService.getEquipesPorSupervisor(eq(102))).thenReturn(List.of(umaEquipeDeVendas()));
        when(agenteAutorizadoNovoService.getUsuariosByAaId(eq(1300), eq(false)))
                .thenReturn(todosUsuariosDoAgenteAutorizado1300ComEquipesDeVendas());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(102).build())))
                .thenReturn(umaPermissaoDeVendaResponse());
        var usuariosParaDistribuicao = usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .hasSize(3)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"))
                .containsSequence(
                    102, "SUPERVISOR",
                    133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR",
                    135, "MARCOS AUGUSTO DA SILVA SANTOS");
    }

    @Test
    public void getUsuariosDisponiveisParaDistribuicao_usuariosDaEquipeVenda_seForSupervisorSemPermissaoDeVenda() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(9991, 999);

        when(usuarioService.getUsuariosAtivosByIds(anyList())).thenReturn(List.of(9991));
        when(usuarioService.findPermissoesByUsuario(any(Usuario.class)))
                .thenReturn(umaPermissaoResponseVazia());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoSupervisor());
        when(equipeVendasService.getEquipesPorSupervisor(eq(102))).thenReturn(List.of(umaEquipeDeVendas()));
        when(equipeVendasService.getByUsuario(eq(9991))).thenReturn(umaEquipeVendasDto());
        when(equipeVendasService.getUsuarioEEquipeByUsuarioIds(anyList())).thenReturn(resultMap);

        var response = usuarioAgendamentoService.recuperarUsuariosDisponiveisParaDistribuicao(999);

        assertThat(response)
                .hasSize(1)
                .extracting(UsuarioAgendamentoResponse::getId, UsuarioAgendamentoResponse::getNome)
                .contains(tuple(9991, "USUARIO 1 DO AA 999"));

        verify(equipeVendasService, times(1))
            .getUsuarioEEquipeByUsuarioIds(List.of(9991));
    }

    @Test
    public void getUsuariosDisponiveisParaDistribuicao_usuariosDaEquipeVendaAndSupervisor_seForSupervisorComPermissaoDeVenda() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(9991, 999);

        when(usuarioService.getUsuariosAtivosByIds(anyList())).thenReturn(List.of(9991));
        when(usuarioService.findPermissoesByUsuario(any(Usuario.class)))
                .thenReturn(umaPermissaoDeVendaResponse());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoSupervisor());
        when(equipeVendasService.getEquipesPorSupervisor(eq(102))).thenReturn(List.of(umaEquipeDeVendas()));
        when(equipeVendasService.getByUsuario(eq(9991))).thenReturn(umaEquipeVendasDto());
        when(equipeVendasService.getUsuarioEEquipeByUsuarioIds(anyList())).thenReturn(resultMap);

        var response = usuarioAgendamentoService.recuperarUsuariosDisponiveisParaDistribuicao(999);

        assertThat(response)
                .hasSize(2)
                .extracting(UsuarioAgendamentoResponse::getId, UsuarioAgendamentoResponse::getNome)
                .contains(tuple(102, "SUPERVISOR"), tuple(9991, "USUARIO 1 DO AA 999"));

        verify(equipeVendasService, times(1))
            .getUsuarioEEquipeByUsuarioIds(List.of(9991));
    }

    @Test
    public void recuperarUsuariosDisponiveisParaDistribuicao_deveRetornarTodosUsuariosDoAA_sePossuirVisualizacaoGeral() {
        when(usuarioService.getUsuariosAtivosByIds(anyList())).thenReturn(List.of(9991, 9992, 9993, 9994, 9995));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoCoordenadorComercial());
        when(equipeVendasService.getByUsuario(eq(9991))).thenReturn(umaEquipeVendasDto());
        when(usuarioRepository.findById(eq(9992))).thenReturn(umUsuarioId9992());
        when(usuarioRepository.findById(eq(9993))).thenReturn(umUsuarioId9993());
        when(usuarioRepository.findById(eq(9994))).thenReturn(umUsuarioId9994());
        when(usuarioRepository.findById(eq(9995))).thenReturn(umUsuarioId9995());

        var response = usuarioAgendamentoService.recuperarUsuariosDisponiveisParaDistribuicao(999);

        assertThat(response)
                .hasSize(5)
                .extracting(UsuarioAgendamentoResponse::getId, UsuarioAgendamentoResponse::getNome)
                .contains(
                        tuple(9991, "USUARIO 1 DO AA 999"),
                        tuple(9992, "USUARIO 2 DO AA 999"),
                        tuple(9993, "USUARIO 3 DO AA 999"),
                        tuple(9994, "USUARIO 4 DO AA 999"),
                        tuple(9995, "USUARIO 5 DO AA 999"));

        verify(equipeVendasService, times(1))
            .getUsuarioEEquipeByUsuarioIds(List.of(9991, 9992, 9993, 9994, 9995));
    }

    @Test
    public void recuperarUsuariosDisponiveisParaDistribuicao_naoDeveLancarException_quandoNaoEncontrarEquipeVenda() {
        when(usuarioService.getUsuariosAtivosByIds(anyList())).thenReturn(List.of(9991, 9992, 9993, 9994, 9995));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCargoCoordenadorComercial());
        when(equipeVendasService.getByUsuario(eq(9991))).thenReturn(umaEquipeVendasDto());
        when(usuarioRepository.findById(eq(9992))).thenReturn(umUsuarioId9992());
        when(usuarioRepository.findById(eq(9993))).thenReturn(umUsuarioId9993());
        when(usuarioRepository.findById(eq(9994))).thenReturn(umUsuarioId9994());
        when(usuarioRepository.findById(eq(9995))).thenReturn(umUsuarioId9995());
        when(equipeVendasService.getUsuarioEEquipeByUsuarioIds(anyList())).thenReturn(Map.of());

        assertThatCode(() -> usuarioAgendamentoService
                .recuperarUsuariosDisponiveisParaDistribuicao(999))
            .doesNotThrowAnyException();

        verify(equipeVendasService, times(1))
            .getUsuarioEEquipeByUsuarioIds(List.of(9991, 9992, 9993, 9994, 9995));
    }

    private EquipeVendasSupervisionadasResponse umaEquipeDeVendas() {
        var equipeVendas = new EquipeVendasSupervisionadasResponse();
        equipeVendas.setId(999);
        equipeVendas.setDescricao("EQUIPE DE VENDAS DO AA 999");
        return equipeVendas;
    }

    private UsuarioAutenticado umUsuarioAutenticadoCargoSupervisor() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(102);
        usuarioAutenticado.setNome("SUPERVISOR");
        usuarioAutenticado.setUsuario(Usuario.builder()
                .id(102)
                .nome("SUPERVISOR")
                .cargo(Cargo
                        .builder()
                        .codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR)
                        .build())
                .build());
        usuarioAutenticado.setCargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR);
        return usuarioAutenticado;
    }

    private UsuarioAutenticado umUsuarioAutenticadoCargoCoordenadorComercial() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(101);
        usuarioAutenticado.setNome("COORDENADOR");
        usuarioAutenticado.setCargoCodigo(CodigoCargo.COORDENADOR_OPERACAO);
        usuarioAutenticado.setDepartamentoCodigo(CodigoDepartamento.COMERCIAL);
        return usuarioAutenticado;
    }

    private UsuarioAutenticado umUsuarioAutenticadoCargoInvalido() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(101);
        usuarioAutenticado.setNome("VENDEDOR");
        usuarioAutenticado.setCargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS);
        return usuarioAutenticado;
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(100);
        usuarioAutenticado.setCargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO);
        usuarioAutenticado.setNome("José");
        return usuarioAutenticado;
    }

    private UsuarioAutenticado umUsuarioAutenticadoSupervisor() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setCargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO);
        usuarioAutenticado.setId(135);
        usuarioAutenticado.setUsuario(Usuario.builder()
                .id(135)
                .nome("MARCOS AUGUSTO DA SILVA SANTOS")
                .cargo(Cargo
                        .builder()
                        .codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO)
                        .build())
                .build());
        usuarioAutenticado.setNome("MARCOS AUGUSTO DA SILVA SANTOS");
        return usuarioAutenticado;
    }

    @Test
    public void getUsuariosParaDistribuicaoByEquipeVendaId_listaUsuarioDistribuicaoResponse_quandoSolicitado() {
        Map<String, Object> filtros = Map.of("equipeVendaId", 100, "ativo", true);

        when(equipeVendasUsuarioService.getAll(filtros))
            .thenReturn(umaListaUsuariosDaEquipeVenda());

        var actual = usuarioAgendamentoService.getUsuariosParaDistribuicaoByEquipeVendaId(100);

        var expected = List.of(
            UsuarioDistribuicaoResponse.builder()
                .id(1)
                .nome("RENATO")
                .build(),
            UsuarioDistribuicaoResponse.builder()
                .id(2)
                .nome("JOAO")
                .build()
        );

        assertThat(actual).isEqualTo(expected);
    }

    private List<EquipeVendaUsuarioResponse> umaListaUsuariosDaEquipeVenda() {
        return List.of(
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(1)
                .usuarioNome("RENATO")
                .equipeVendaId(10)
                .build(),
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(2)
                .usuarioNome("JOAO")
                .equipeVendaId(10)
                .build()
        );
    }
}
