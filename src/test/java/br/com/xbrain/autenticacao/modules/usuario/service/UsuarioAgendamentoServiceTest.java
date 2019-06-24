package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.mailing.service.TabulacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoListagemResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.DistribuirTabulacoesMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.AgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({UsuarioAgendamentoService.class})
public class UsuarioAgendamentoServiceTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private ColaboradorVendasService colaboradorVendasService;
    @MockBean
    private TabulacaoService tabulacaoService;
    @MockBean
    private DistribuirTabulacoesMqSender distribuirTabulacoesMqSender;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private CargoService cargoService;
    @Autowired
    private UsuarioAgendamentoService usuarioAgendamentoService;

    @Before
    public void setup() {
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(130)))
                .thenReturn(agendamentosDoUsuario130PorAa());
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(132)))
                .thenReturn(agendamentosDoUsuario130PorAa());
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(131)))
                .thenReturn(List.of(new AgendamentoAgenteAutorizadoResponse(1300, 0L)));
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(140)))
                .thenReturn(agendamentosDoAA1400());
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(141)))
                .thenReturn(agendamentosDoAA1400());
        when(tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(eq(150)))
                .thenReturn(List.of(new AgendamentoAgenteAutorizadoResponse(1500, 240L)));
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos())
                .thenReturn(agentesAutorizadosPermitidos());
        when(agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(eq(1300), any()))
                .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1300());
        when(agenteAutorizadoService.getUsuariosByAaId(eq(1300), eq(false)))
                .thenReturn(todosUsuariosDoAgenteAutorizado1300());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(133).build())))
                .thenReturn(umaPermissaoDeVendaResponse());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(135).build())))
                .thenReturn(umaPermissaoResponseVazia());
        when(agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(eq(1400), any()))
                .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1400());
        when(agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(eq(1500), any()))
                .thenReturn(usuariosDoAgenteAutorizado1500());
        when(colaboradorVendasService.getEquipeVendasSupervisorDoUsuarioId(any()))
                .thenReturn(Optional.ofNullable(umaEquipeVendaAgendamentoRespose()));
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticado());
        when(cargoService.findByUsuarioId(eq(130)))
                .thenReturn(umCargoVendedorTelevendas());
        when(cargoService.findByUsuarioId(eq(132)))
                .thenReturn(umCargoSocioPrincipal());
        when(cargoService.findByUsuarioId(eq(150)))
                .thenReturn(umCargoVendedorTelevendas());
    }

    @Test
    public void distribuirAgendamentosDoUsuario_deveLancarExcecao_seQuantidadeForEnviadaForDiferenteDoTotal() {
        assertThatExceptionOfType(ValidacaoException.class)
                .isThrownBy(() -> usuarioAgendamentoService.distribuirAgendamentosDoUsuario(
                        umAgendamentoDistribuicaoRequestDoUsuario140()))
                .withMessage("Quantidade de agendamentos enviada é inválida.");
    }

    @Test
    public void distribuirAgendamentosDoUsuario_deveDistribuirTabulacoes_seQuantidadeForValida() {
        usuarioAgendamentoService.distribuirAgendamentosDoUsuario(umAgendamentoDistribuicaoRequestDoUsuario141());
        verify(tabulacaoService, times(1)).getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(argThat(new AssertionMatcher<>() {
            @Override
            public void assertion(Integer usuarioId) throws AssertionError {
                assertThat(usuarioId).isEqualTo(141);
            }
        }));
        verify(distribuirTabulacoesMqSender, times(1)).distribuirTabulacoes(argThat(new AssertionMatcher<>() {
            @Override
            public void assertion(TabulacaoDistribuicaoMqRequest request) throws AssertionError {
                assertThat(request)
                    .extracting("agenteAutorizadoId", "usuarioOrigemId")
                    .containsExactly(1400, 141);
                assertThat(request.getColaboradores())
                    .extracting("id", "nome", "quantidade")
                    .containsExactlyInAnyOrder(
                            tuple(140, "USUARIO 140", 5L),
                            tuple(142, "USUARIO 142", 5L),
                            tuple(143, "USUARIO 143", 4L));
            }
        }));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarSupervisor_seNaoPossuirPermissaoDeVenda() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(130);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome")
                .doesNotContain(tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedores_seUsuarioForDeCargoHibrido() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(132);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome")
                .doesNotContain(
                        tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"),
                        tuple(131, "JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR"),
                        tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS"),
                        tuple(134, "MARIA DA SILVA SAURO SANTOS"),
                        tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS"))
                .contains(
                        tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedor_seForDeOutroSegmento() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(130);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome")
                .doesNotContain(tuple(131, "JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedor_seForMesmoUsuario() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(130);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome")
                .doesNotContain(tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_deveRetornarAgendamentosPorUsuario_seExistiremAgendamentos() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(130);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .extracting("agenteAutorizadoId", "quantidadeAgendamentos", "quantidadeAgendamentosRestantes")
                .containsExactlyInAnyOrder(tuple(1300, 35L, 2L));

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome", "quantidade")
                .containsExactlyInAnyOrder(
                        tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS", 11L),
                        tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", 11L),
                        tuple(134, "MARIA DA SILVA SAURO SANTOS", 11L));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_deveRetornarNenhumAgendamento_seNaoExistiremAgendamentos() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(131);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .extracting("agenteAutorizadoId", "quantidadeAgendamentos", "quantidadeAgendamentosRestantes")
                .containsExactly(tuple(1300, 0L, 0L));

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting("agendamentosPorUsuarios")
                .extracting("id", "nome", "quantidade")
                .containsExactlyInAnyOrder(
                        tuple(132, "JOSÉ MARINHO DA SILVA DOS SANTOS", 0L),
                        tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", 0L));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_deveDistribuirIgualmente_seDivisaoDeAgendamentosPorUsuariosForExata() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(todosUsuariosDoAgenteAutorizado1500());

        List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoDoUsuario =
                usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(150);

        assertThat(agendamentoDistribuicaoDoUsuario)
                .extracting("agenteAutorizadoId", "quantidadeAgendamentos", "quantidadeAgendamentosRestantes")
                .containsExactly(tuple(1500, 240L, 0L));

        assertThat(agendamentoDistribuicaoDoUsuario)
                .flatExtracting(AgendamentoDistribuicaoListagemResponse::getAgendamentosPorUsuarios)
                .extracting(AgendamentoUsuarioDto::getQuantidade)
                .haveExactly(8, QUANTIDADE_IGUAL_A.apply(30L));

    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(100);
        usuarioAutenticado.setNome("José");
        return usuarioAutenticado;
    }
}
