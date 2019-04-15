package br.com.xbrain.autenticacao.modules.usuario.service;

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
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioEquipeVendaMqSender;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.EXECUTIVO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
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
    @MockBean
    private UsuarioEquipeVendaMqSender equipeVendaMqSender;
    @SpyBean
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
    public void deveNaoSalvarUsuarioEEnviarParaFilaDeFalha() {
        try {
            service.saveFromQueue(new UsuarioMqRequest());
        } catch (Exception exception) {
            verify(sender, times(1)).sendWithFailure(any());
        }
    }

    @Test
    public void deveAlterarOCargoDoUsuario() {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setCargo(EXECUTIVO);
        service.alterarCargoUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getCargoCodigo(), EXECUTIVO);
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
    public void inativar_deveInativarUmUsuario_seAtivo() {

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(100);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
        verify(equipeVendaMqSender, times(0)).sendInativar(any());
    }

    @Test
    public void inativar_deveNaoEnviarParaInativarNoEquipeVendas_sePossuirCargoSupervisor() {
        doReturn(umUsuarioSupervisor()).when(service).findComplete(205);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(205);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, times(0)).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoAssistente() {
        doReturn(umUsuarioAssistente()).when(service).findComplete(204);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(204);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, times(1)).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoVendedorD2d() {
        doReturn(umUsuarioVendedorD2d()).when(service).findComplete(203);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(203);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, times(1)).sendInativar(any());
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
    public void updateFromQueue_deveAlterarCpf_seNovoCpfValido() throws Exception {
        UsuarioMqRequest usuarioMqRequest = umUsuarioARealocar();
        usuarioMqRequest.setId(368);
        usuarioMqRequest.setCpf("43185104099");
        service.updateFromQueue(usuarioMqRequest);
        Usuario usuario = usuarioRepository
            .findTop1UsuarioByCpf("43185104099").orElseThrow(() -> new ValidacaoException("Usuário não encontrado"));
        assertNotNull(usuario);
    }

    @Test
    public void updateFromQueue_deveLancarException_seNovoCpfInvalido() throws Exception {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(368);
        usuarioDto.setCpf("41842888803");
        assertThatExceptionOfType(ValidacaoException.class)
                .isThrownBy(() -> service.saveUsuarioAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto)))
                .withMessage("CPF já cadastrado.");
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
    public void deveEnviarAFilaDeErrosAoRecuperarUsuariosAgentesAutorizados() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(104);
        usuarioMqRequest.setCpf("2292929292929292929229292929");
        usuarioMqRequest.setCargo(EXECUTIVO);
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

    private Usuario umUsuarioSupervisor() {
        var usuario = usuarioRepository.findOne(110);
        usuario.setCargo(cargoRepository.findByCodigo(CodigoCargo.SUPERVISOR_OPERACAO));

        return usuario;
    }

    private Usuario umUsuarioAssistente() {
        var usuario = usuarioRepository.findOne(100);
        usuario.setCargo(cargoRepository.findByCodigo(CodigoCargo.ASSISTENTE_OPERACAO));

        return usuario;
    }

    private Usuario umUsuarioVendedorD2d() {
        var usuario = usuarioRepository.findOne(100);
        usuario.setCargo(cargoRepository.findByCodigo(CodigoCargo.VENDEDOR_OPERACAO));

        return usuario;
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
}
