package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaClient;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.AtualizarUsuarioMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioCadastroMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioRecuperacaoMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql"})
public class UsuarioServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @MockBean
    private UsuarioCadastroMqSender sender;
    @MockBean
    private AtualizarUsuarioMqSender atualizarUsuarioMqSender;
    @MockBean
    private UsuarioRecuperacaoMqSender usuarioRecuperacaoMqSender;
    @Autowired
    private UsuarioService service;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private AgenteAutorizadoClient agenteAutorizadoClient;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @MockBean
    private NotificacaoService notificacaoService;
    @MockBean
    private EquipeVendaClient equipeVendaClient;

    @Before
    public void setUp() {
        when(autenticacaoService.getUsuarioId()).thenReturn(101);
    }

    @Test
    public void deveNaoEnviarEmailQuandoNaoSalvarUsuario() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setCpf("2292929292929292929229292929");
        service.saveFromQueue(usuarioMqRequest);
        verify(sender, times(0)).sendSuccess(any());
        verify(emailService, times(0)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveSalvarUsuarioEEnviarParaFila() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        service.saveFromQueue(usuarioMqRequest);
        UsuarioDto usuarioDto = service.findByEmail(usuarioMqRequest.getEmail());
        Assert.assertEquals(usuarioDto.getCpf(), usuarioMqRequest.getCpf());
        verify(sender, times(1)).sendSuccess(any());
    }

    @Test
    public void getUsuariosByCidades_recuperarTodosOsAssistentesDasCidades_seExistirUsuarios() {
        List<UsuarioResponseD2D> assistentesOperacao = service.getUsuariosBySupervisorId(100);
        Assert.assertEquals(assistentesOperacao.size(), 5);
    }

    @Test
    public void getUsuariosByCidades_recuperarTodosOsSupervisoresDasCidades_seExistirUsuarios() {
        List<UsuarioResponseD2D> supervisoresOperacao = service.getUsuariosBySupervisorId(100);
        Assert.assertEquals(supervisoresOperacao.size(), 5);
    }

    @Test
    public void getUsuariosByCidades_recuperarTodosOsVendedoresDasCidades_seExistirUsuarios() {
        List<UsuarioResponseD2D> vendedoresOperacao = service.getUsuariosBySupervisorId(100);
        Assert.assertEquals(vendedoresOperacao.size(), 5);
    }

    @Test
    public void deveNaoSalvarUsuarioEEnviarParaFilaDeFalha() {
        try {
            service.saveFromQueue(new UsuarioMqRequest());
        } catch (Exception exception) {
            verify(sender, times(1)).sendWithFailure(any());
        }
    }

    @Test
    public void deveBuscarSuperioresDoUsuario() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = getFiltroHierarquia();
        List<UsuarioResponse> usuariosResponse = service.getUsuariosSuperiores(getFiltroHierarquia());
        Assert.assertEquals(1, usuariosResponse.size());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoCargo(), usuarioFiltrosHierarquia.getCodigoCargo());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoDepartamento(), usuarioFiltrosHierarquia.getCodigoDepartamento());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoNivel(), usuarioFiltrosHierarquia.getCodigoNivel());
    }

    @Test
    public void deveAlterarOCargoDoUsuario() {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setCargo(CodigoCargo.EXECUTIVO);
        service.alterarCargoUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getCargoCodigo(), CodigoCargo.EXECUTIVO);
    }

    @Test
    public void deveAlterarOEmailDoUsuario() throws Exception {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setEmail("EMAILALTERADO@XBRAIN.COM.BR");
        service.alterarEmailUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getEmail(), "EMAILALTERADO@XBRAIN.COM.BR");
    }

    @Test
    public void deveInativarUmUsuario() throws Exception {
        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(100);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
    }

    @Test
    public void salvarUsuarioRealocado_deveRealocarUsuario_quandoUsuarioEstiverAtivo() throws Exception {
        Usuario usuarioRealocar = new Usuario();
        usuarioRealocar.setId(366);
        service.salvarUsuarioRealocado(usuarioRealocar);
        Assert.assertEquals(ESituacao.R, usuarioRepository.findById(usuarioRealocar.getId()).get().getSituacao());
    }

    @Test
    public void updateFromQueue_deveCriarNovoUsuario_quandoAntigoRealocado() throws Exception {
        UsuarioMqRequest usuarioMqRequest = umUsuarioARealocar();
        usuarioMqRequest.setId(368);
        service.updateFromQueue(usuarioMqRequest);
        usuarioRepository.findAllByCpf("21145664523")
                .forEach(usuario -> {
                    if (usuario.getSituacao().equals(ESituacao.A)) {
                        Assert.assertEquals(ESituacao.A, usuario.getSituacao());
                    } else if (usuario.getSituacao().equals(ESituacao.R)) {
                        Assert.assertEquals(ESituacao.R, usuario.getSituacao());
                    }
                });
        Assert.assertEquals(2, usuarioRepository.findAllByCpf("21145664523").size());
    }

    @Test
    public void updateFromQueue_NaoRealocaUsuario_QuandoSituacaoForInativa() throws Exception {
        service.updateFromQueue(umUsuarioInativo());
        List<Usuario> usuarios = usuarioRepository.findAllByCpf("41842888803");
        Assert.assertEquals(ESituacao.I, usuarios.get(0).getSituacao());
        Assert.assertEquals(1, usuarios.size());
    }

    @Test
    public void updateFromQueue_NaoRealocaUsuario_QuandoAFlagRealocadoForFalse() throws Exception {
        UsuarioMqRequest naoRealocar = umUsuarioARealocar();
        naoRealocar.setRealocado(false);
        service.updateFromQueue(naoRealocar);
        List<Usuario> usuarios = usuarioRepository.findAllByCpf("21145664523");
        assertThat(usuarios)
                .extracting(Usuario::getSituacao)
                .containsOnly(ESituacao.A);
    }

    @Test
    public void naoDeveAtivarUmUsuarioQuandoAgenteAutorizadoInativo() throws Exception {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("O usuário não pode ser ativo, porque o Agente Autorizado está inativo.");
        UsuarioAtivacaoDto usuarioAtivacaoDto = new UsuarioAtivacaoDto();
        usuarioAtivacaoDto.setIdUsuario(243);
        usuarioAtivacaoDto.setObservacao("Teste ativar");
        service.ativar(usuarioAtivacaoDto);
    }

    @Test
    public void deveAlterarSenhaUsuario() throws Exception {
        UsuarioAlterarSenhaDto usuarioAlterarSenhaDto = new UsuarioAlterarSenhaDto();
        usuarioAlterarSenhaDto.setUsuarioId(100);
        usuarioAlterarSenhaDto.setAlterarSenha(Eboolean.V);
        service.alterarSenhaAa(usuarioAlterarSenhaDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getAlterarSenha(), Eboolean.V);
    }

    @Test
    public void deveLimparCpfDeUmUsuario() {
        service.limparCpfUsuario(100);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getCpf(), null);
    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloProprioUsuarioFicarEmLoop() throws Exception {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível adicionar o usuário ADMIN como superior,"
                + " pois o usuário mso_analistaadm_claromovel_pessoal é superior a ele em sua hierarquia.");
        Usuario usuario = umUsuarioComLoopNaHierarquia();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloProprioUsuarioFicarEmLoopCom2Niveis() throws Exception {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível adicionar o usuário ADMIN como superior,"
                + " pois o usuário INATIVO é superior a ele em sua hierarquia.");
        Usuario usuario = umUsuarioComProximoUsuarioComoSuperior();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloUsuarioSerSeuSuperior() throws Exception {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível atrelar o próprio usuário em sua Hierarquia.");
        Usuario usuario = umUsuarioComProprioUsuarioComoSuperior();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveEditarHierarquiaSemExceptions() throws Exception {
        Usuario usuario = umUsuarioComHierarquia();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveBuscarOsUsuarioComInatividade() throws Exception {
        List<Usuario> usuarios = service.getUsuariosSemAcesso();
        Assert.assertEquals(2, usuarios
                .stream()
                .filter(u -> Arrays.asList(104, 101).contains(u.getId()))
                .collect(Collectors.toList())
                .size());
    }

    @Test
    public void deveInativarOsUsuarioComInatividade() throws Exception {
        service.inativarUsuariosSemAcesso();
        List<Usuario> usuarios = service.getUsuariosSemAcesso();
        Assert.assertEquals(0, usuarios.size());

        Assert.assertEquals(ESituacao.I, service.findById(101).getSituacao());
        Assert.assertEquals(ESituacao.I, service.findById(104).getSituacao());

        Assert.assertEquals(1, usuarioHistoricoService.getHistoricoDoUsuario(101)
                .stream().filter(h -> "Inativado por falta de acesso".equals(h.getObservacao())).count());

        Assert.assertEquals(1, usuarioHistoricoService.getHistoricoDoUsuario(104)
                .stream().filter(h -> "Inativado por falta de acesso".equals(h.getObservacao())).count());

        Assert.assertEquals(ESituacao.A, service.findById(100).getSituacao());
        Assert.assertEquals(ESituacao.A, service.findById(366).getSituacao());
    }

    @Test
    public void deveEnviarAFilaDeErrosAoRecuperarUsuariosAgentesAutorizados() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(104);
        usuarioMqRequest.setCpf("2292929292929292929229292929");
        usuarioMqRequest.setCargo(CodigoCargo.EXECUTIVO);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.AGENTE_AUTORIZADO);
        service.recuperarUsuariosAgentesAutorizados(usuarioMqRequest);

        verify(usuarioRecuperacaoMqSender, times(1)).sendWithFailure(any());
    }

    @Test
    public void deveEnviarFilaDeAtualizarUsuariosNoPolQuandoForSocioPrincipal() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(368);
        usuarioMqRequest.setCpf("21145664523");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.AGENTE_AUTORIZADO);
        usuarioMqRequest.setSituacao(ESituacao.A);
        service.saveFromQueue(usuarioMqRequest);

        verify(atualizarUsuarioMqSender, times(1)).sendSuccess(any());
    }

    @Test
    public void naoDeveEnviarFilaDeAtualizarUsuariosNoPolQuandoNaoForSocioPrincipal() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(104);
        usuarioMqRequest.setCpf("21145664523");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_ACEITE);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.AGENTE_AUTORIZADO);
        usuarioMqRequest.setSituacao(ESituacao.A);

        service.saveFromQueue(usuarioMqRequest);

        verify(atualizarUsuarioMqSender, times(0)).sendSuccess(any());
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForGerenteOperacaoPelaHierarquia() {
        Assert.assertEquals(5, service.getVendedoresOperacaoDaHierarquia(227).size());
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForOperacaoPelaHierarquia() {
        Assert.assertEquals(3, service.getVendedoresOperacaoDaHierarquia(228).size());
        Assert.assertEquals(2, service.getVendedoresOperacaoDaHierarquia(234).size());
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForVendedorOperacaoPelaHierarquia() {
        Assert.assertEquals(0, service.getVendedoresOperacaoDaHierarquia(230).size());
    }

    @Test
    public void getIdsSubordinadosDaHierarquia_idsDosVendedores_quandoForGerente() {
        Assert.assertEquals(3, service.getIdsSubordinadosDaHierarquia(227,
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
    }

    @Test
    public void getIdsSubordinadosDaHierarquia_idsDosVendedores_quandoForCoordenador() {
        Assert.assertEquals(2, service.getIdsSubordinadosDaHierarquia(228,
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
        Assert.assertEquals(1, service.getIdsSubordinadosDaHierarquia(234,
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
    }

    @Test
    public void getAllForCsv_ListaComUsuariosParaExportacaoCsv_ComFiltroPorNomeUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        List<UsuarioCsvResponse> usuarios = service.getAllForCsv(getFiltroUsuario("USUARIO TESTE"));
        assertEquals(1, usuarios.size());
        assertEquals("USUARIO TESTE", usuarios.get(0).getNome());
        assertEquals("USUARIO_TESTE@GMAIL.COM", usuarios.get(0).getEmail());
        assertEquals("Xbrain.NET", usuarios.get(0).getEmpresas());
        assertEquals("Pessoal.Xbrain", usuarios.get(0).getUnidadesNegocios());
        assertEquals("Vendedor", usuarios.get(0).getCargo());
        assertEquals("Administrador", usuarios.get(0).getDepartamento());
    }

    private UsuarioMqRequest umUsuarioARealocar() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(104);
        usuarioMqRequest.setCpf("21145664523");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.HELP_DESK);
        usuarioMqRequest.setSituacao(ESituacao.A);
        usuarioMqRequest.setRealocado(true);
        return usuarioMqRequest;
    }

    private UsuarioMqRequest umUsuarioInativo() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(105);
        usuarioMqRequest.setCpf("41842888803");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.HELP_DESK);
        usuarioMqRequest.setSituacao(ESituacao.I);
        usuarioMqRequest.setRealocado(true);
        return usuarioMqRequest;
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return new UsuarioAutenticado(umUsuarioComHierarquia());
    }

    private UsuarioFiltros getFiltroUsuario(String nome) {
        UsuarioFiltros usuarioFiltros = new UsuarioFiltros();
        usuarioFiltros.setNome(nome);
        return usuarioFiltros;
    }

    private Usuario umUsuarioComHierarquia() {
        Usuario usuario = usuarioRepository.findOne(110);
        UsuarioHierarquia usuarioHierarquia = criarUsuarioHierarquia(usuario, 113);
        usuario.getUsuariosHierarquia().add(usuarioHierarquia);
        return usuario;
    }

    private Usuario umUsuarioComProprioUsuarioComoSuperior() {
        Usuario usuario = usuarioRepository.findOne(110);
        UsuarioHierarquia usuarioHierarquia = criarUsuarioHierarquia(usuario, usuario.getId());
        usuario.getUsuariosHierarquia().add(usuarioHierarquia);
        return usuario;
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idUsuarioSuperior) {
        return UsuarioHierarquia.criar(usuario, idUsuarioSuperior, usuario.getId());
    }

    private Usuario umUsuarioComLoopNaHierarquia() {
        Usuario user = usuarioRepository.findOne(114);
        UsuarioHierarquia usuarioHierarquia = criarUsuarioHierarquia(user, 110);
        user.getUsuariosHierarquia().add(usuarioHierarquia);
        return user;
    }

    private Usuario umUsuarioComProximoUsuarioComoSuperior() {
        Usuario user = usuarioRepository.findOne(112);
        UsuarioHierarquia usuarioHierarquia = criarUsuarioHierarquia(user, 110);
        user.getUsuariosHierarquia().add(usuarioHierarquia);
        return user;
    }

    private UsuarioMqRequest umUsuario() {
        UsuarioMqRequest usuarioMqRequest = new UsuarioMqRequest();
        usuarioMqRequest.setNome("TESTE NOVO USUARIO PARCEIROS ONLINE");
        usuarioMqRequest.setEmail("novousuarioparceirosonline@xbrain.com.br");
        usuarioMqRequest.setCpf("76696512616");
        usuarioMqRequest.setUnidadesNegocio(Collections.singletonList(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS));
        usuarioMqRequest.setNivel(CodigoNivel.AGENTE_AUTORIZADO);
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.AGENTE_AUTORIZADO);
        usuarioMqRequest.setEmpresa(Collections.singletonList(CodigoEmpresa.CLARO_MOVEL));
        usuarioMqRequest.setUsuarioCadastroId(100);
        usuarioMqRequest.setRealocado(false);
        return usuarioMqRequest;
    }

    private UsuarioFiltrosHierarquia getFiltroHierarquia() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = new UsuarioFiltrosHierarquia();
        usuarioFiltrosHierarquia.setUsuarioId(Collections.singletonList(101));
        usuarioFiltrosHierarquia.setCodigoNivel(CodigoNivel.OPERACAO);
        usuarioFiltrosHierarquia.setCodigoDepartamento(CodigoDepartamento.COMERCIAL);
        usuarioFiltrosHierarquia.setCodigoCargo(CodigoCargo.GERENTE_OPERACAO);
        return usuarioFiltrosHierarquia;
    }

}
