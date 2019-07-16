package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
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

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({UsuarioAgendamentoService.class})
public class UsuarioAgendamentoServiceTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
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
        when(agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(eq(1300), any()))
                .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1300());
        when(agenteAutorizadoService.getUsuariosByAaId(eq(1300), eq(false)))
                .thenReturn(todosUsuariosDoAgenteAutorizado1300());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(133).build())))
                .thenReturn(umaPermissaoDeVendaResponse());
        when(usuarioService.findPermissoesByUsuario(eq(Usuario.builder().id(135).build())))
                .thenReturn(umaPermissaoResponseVazia());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(cargoService.findByUsuarioId(eq(130))).thenReturn(umCargoVendedorTelevendas());
        when(cargoService.findByUsuarioId(eq(132))).thenReturn(umCargoSocioPrincipal());
        when(cargoService.findByUsuarioId(eq(150))).thenReturn(umCargoVendedorTelevendas());
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos()).thenReturn(agentesAutorizadosPermitidos());
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarSupervisor_seNaoPossuirPermissaoDeVenda() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosParaDistribuicao =
                usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedores_seUsuarioForDeCargoHibrido() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosParaDistribuicao =
                usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(132, 1300);

        assertThat(usuariosParaDistribuicao)
                .extracting("id", "nome")
                .containsOnly(tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedor_seForDeOutroSegmento() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosParaDistribuicao =
                usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(131, "JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR"));
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_naoDeveMostrarVendedor_seForMesmoUsuario() {
        when(usuarioRepository.getUsuariosFilter(any()))
                .thenReturn(usuariosDoAgenteAutorizado1300());

        List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosParaDistribuicao =
                usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(130, 1300);

        assertThat(usuariosParaDistribuicao)
                .flatExtracting("id", "nome")
                .doesNotContain(tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS"));
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(100);
        usuarioAutenticado.setNome("José");
        return usuarioAutenticado;
    }
}
