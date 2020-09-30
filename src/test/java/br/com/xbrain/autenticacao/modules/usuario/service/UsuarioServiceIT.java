package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dClient;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa.CLARO_TV;
import static br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio.CLARO_RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql",
    "classpath:/tests_usuario_remanejamento.sql"})
public class UsuarioServiceIT {

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
    private EquipeVendaD2dClient equipeVendaD2dClient;
    @MockBean
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @MockBean
    private UsuarioFeriasService usuarioFeriasService;
    @MockBean
    private UsuarioFeederCadastroSucessoMqSender usuarioFeederCadastroSucessoMqSender;
    @MockBean
    private FeederService feederService;

    @Before
    public void setUp() {
        when(autenticacaoService.getUsuarioId()).thenReturn(101);
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.of(101));
    }

    @Test
    public void deveNaoEnviarEmailQuandoNaoSalvarUsuario() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setCpf("2292929292929292929229292929");
        service.saveFromQueue(usuarioMqRequest);
        verify(sender, times(0)).sendSuccess(any());
        verify(emailService, times(0)).enviarEmailTemplate(any(), any(), any(), any());
        verify(feederService, never()).adicionarPermissaoFeederParaUsuarioNovo(any(), any());
    }

    @Test
    public void deveSalvarUsuarioEEnviarParaFila() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        service.saveFromQueue(usuarioMqRequest);
        UsuarioDto usuarioDto = service.findByEmail(usuarioMqRequest.getEmail());
        assertEquals(usuarioDto.getCpf(), usuarioMqRequest.getCpf());
        verify(sender, times(1)).sendSuccess(any());
        verify(feederService, times(1)).adicionarPermissaoFeederParaUsuarioNovo(any(), any());
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
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getCargoCodigo(), EXECUTIVO);
    }

    @Test
    public void deveAlterarOEmailDoUsuario() {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setEmail("EMAILALTERADO@XBRAIN.COM.BR");
        service.alterarEmailUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getEmail(), "EMAILALTERADO@XBRAIN.COM.BR");
    }

    @Test
    public void inativar_deveInativarUmUsuario_seAtivoEProvenienteDaFila() {
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.empty());
        var usuarioInativacao = UsuarioInativacaoDto
            .builder()
            .idUsuario(100)
            .codigoMotivoInativacao(CodigoMotivoInativacao.DESCREDENCIADO)
            .idUsuarioInativacao(101)
            .build();
        service.inativar(usuarioInativacao);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getSituacao(), ESituacao.I);

        assertThat(usuario.getHistoricos()).isNotNull();
        assertThat(usuario.getHistoricos())
            .extracting("situacao", "motivoInativacao.codigo")
            .containsAnyOf(
                tuple(ESituacao.I, CodigoMotivoInativacao.DESCREDENCIADO)
            );

        verify(equipeVendaMqSender, never()).sendInativar(any());
    }

    @Test
    public void inativar_deveInativarUmUsuario_seAtivoEComMotivoInativo() {
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.empty());
        var usuarioInativacao = UsuarioInativacaoDto
            .builder()
            .idUsuario(100)
            .codigoMotivoInativacao(CodigoMotivoInativacao.INATIVO)
            .idUsuarioInativacao(101)
            .build();
        service.inativar(usuarioInativacao);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getSituacao(), ESituacao.I);
        verify(equipeVendaMqSender, never()).sendInativar(any());
    }

    @Test
    public void inativar_deveInativarUmUsuario_seAtivo() {
        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(100);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getSituacao(), ESituacao.I);

        assertThat(usuario.getHistoricos()).isNotNull();
        assertThat(usuario.getHistoricos())
            .extracting("situacao", "observacao", "motivoInativacao.codigo")
            .containsAnyOf(
                tuple(ESituacao.I, "Teste inativar", CodigoMotivoInativacao.FERIAS)
            );

        verify(equipeVendaMqSender, never()).sendInativar(any());
    }

    @Test
    public void inativar_deveGerarUsuarioFerias_quandoOMotivoDaInativacaoForFerias() {
        service.inativar(UsuarioInativacaoDto
                .builder()
                .idUsuario(100)
                .codigoMotivoInativacao(CodigoMotivoInativacao.FERIAS)
                .dataInicio(LocalDate.of(2019, 1, 1))
                .dataFim(LocalDate.of(2019, 2, 1))
                .build());

        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getSituacao(), ESituacao.I);

        assertThat(usuario.getHistoricos()).isNotNull();
        assertThat(usuario.getHistoricos())
            .extracting("situacao", "motivoInativacao.codigo")
            .containsAnyOf(
                tuple(ESituacao.I, CodigoMotivoInativacao.FERIAS)
            );

        verify(usuarioFeriasService, atLeastOnce()).save(eq(usuario), any());
    }

    @Test
    public void inativar_deveNaoEnviarParaInativarNoEquipeVendas_sePossuirCargoGerente() {
        doReturn(umUsuarioGerente()).when(service).findComplete(227);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(227);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);

        var usuarioCompleto = usuarioRepository.findById(227).get();

        assertThat(usuarioCompleto.getHistoricos()).isNotNull();
        assertThat(usuarioCompleto.getHistoricos())
            .extracting("situacao", "observacao", "motivoInativacao.codigo")
            .containsAnyOf(
                tuple(ESituacao.I, "Teste inativar", CodigoMotivoInativacao.FERIAS)
            );

        verify(equipeVendaMqSender, never()).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoSupervisor() {
        doReturn(umUsuarioSupervisor()).when(service).findComplete(205);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(205);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.DEMISSAO);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoAssistenteComMotivoDemissao() {
        doReturn(umUsuarioAssistente()).when(service).findComplete(204);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(204);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.DEMISSAO);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, atLeastOnce()).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoVendedorD2dComMotivoDemissao() {
        doReturn(umUsuarioVendedorD2d()).when(service).findComplete(203);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(203);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.DEMISSAO);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, atLeastOnce()).sendInativar(any());
    }

    @Test
    public void updateFromQueue_deveAlterarCpf_seNovoCpfValido() throws Exception {
        UsuarioMqRequest usuarioMqRequest = umUsuarioTrocaCpf();
        usuarioMqRequest.setId(368);
        usuarioMqRequest.setCpf("43185104099");
        service.updateFromQueue(usuarioMqRequest);
        Usuario usuario = usuarioRepository
                .findTop1UsuarioByCpf("43185104099").orElseThrow(() -> new ValidacaoException("Usuário não encontrado"));
        assertThat(usuario).isNotNull();
        assertThat(usuario.getCpf()).isEqualTo("43185104099");
        assertThat(usuario.getHistoricos()).isNotNull();
        assertThat(usuario.getHistoricos())
            .extracting("situacao", "observacao")
            .containsExactlyInAnyOrder(
                tuple(ESituacao.A, "Alteração de CPF do usuário.")
            );
    }

    @Test
    public void updateFromQueue_deveLancarException_seNovoCpfInvalido() throws Exception {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(368);
        usuarioDto.setCpf("41842888803");
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.saveUsuarioAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto)))
            .withMessage("CPF já cadastrado.");
        verify(sender, times(0)).sendSuccess(any(UsuarioDto.class));
    }

    @Test
    public void ativar_deveAtivarUsuario_quandoAaNaoEstiverInativoOuDescredenciadoEEmailDoSocioSerIgualAoVinculadoNoAa() {
        when(agenteAutorizadoClient.existeAaAtivoBySocioEmail(anyString())).thenReturn(true);
        service.ativar(UsuarioAtivacaoDto.builder()
                .idUsuario(245)
                .observacao("ATIVANDO O SÓCIO PRINCIPAL")
                .build());

        Usuario usuarioLocalizado = usuarioRepository.findById(245).get();
        assertThat(usuarioLocalizado)
                .extracting("id", "nome", "email", "cpf", "situacao")
                .containsExactly(245, "ALBERTO ALVES", "ALBERTO_AA_@GMAIL.COM", "45723327708", ESituacao.A);
        assertThat(usuarioLocalizado.getHistoricos())
                .extracting("usuario.id", "motivoInativacao", "usuarioAlteracao.id", "observacao", "situacao")
                .containsExactly(tuple(245, null, 101, "ATIVANDO O SÓCIO PRINCIPAL", ESituacao.A));
    }

    @Test
    public void ativar_deveRetornarException_quandoUsuarioNaoPossuirCpf() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("O usuário não pode ser ativado por não possuir CPF.");
        service.ativar(UsuarioAtivacaoDto.builder()
                .idUsuario(246)
                .observacao("ATIVANDO UM USUÁRIO")
                .build());
    }

    @Test
    public void ativar_deveRetornarException_quandoAtivarUmSocioQuandoAaEstaInativoOuDescredenciadoOuComEmailDivergente() {
        when(agenteAutorizadoClient.existeAaAtivoBySocioEmail(anyString())).thenReturn(false);
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Erro ao ativar, o agente autorizado está inativo ou descredenciado."
                + " Ou email do sócio está divergente do que está inserido no agente autorizado.");
        service.ativar(UsuarioAtivacaoDto.builder()
                .idUsuario(245)
                .observacao("ATIVANDO O SÓCIO PRINCIPAL")
                .build());
    }

    @Test
    public void ativar_deveRetornarException_quandoOAaDoUsuarioEstiverInativoOuDescredenciado() {
        when(agenteAutorizadoClient.existeAaAtivoByUsuarioId(anyInt())).thenReturn(false);
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Erro ao ativar, o agente autorizado está inativo ou descredenciado.");
        service.ativar(UsuarioAtivacaoDto.builder()
                .idUsuario(243)
                .observacao("Teste ativar")
                .build());
    }

    @Test
    public void ativar_deveAtivarUmUsuario_quandoNaoForAgenteAutorizado() {
        assertEquals(usuarioRepository.findById(244).orElse(null).getSituacao(), ESituacao.I);
        UsuarioAtivacaoDto usuarioAtivacaoDto = new UsuarioAtivacaoDto();
        usuarioAtivacaoDto.setIdUsuario(244);
        usuarioAtivacaoDto.setObservacao("Teste ativar");
        service.ativar(usuarioAtivacaoDto);
        assertEquals(usuarioRepository.findById(244).orElse(null).getSituacao(), ESituacao.A);
    }

    @Test
    public void deveAlterarSenhaUsuario() {
        UsuarioAlterarSenhaDto usuarioAlterarSenhaDto = new UsuarioAlterarSenhaDto();
        usuarioAlterarSenhaDto.setUsuarioId(100);
        usuarioAlterarSenhaDto.setAlterarSenha(Eboolean.V);
        service.alterarSenhaAa(usuarioAlterarSenhaDto);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getAlterarSenha(), Eboolean.V);
    }

    @Test
    public void deveLimparCpfDeUmUsuario() {
        service.limparCpfUsuario(100);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getCpf(), null);
    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloProprioUsuarioFicarEmLoop() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível adicionar o usuário ADMIN como superior,"
                + " pois o usuário mso_analistaadm_claromovel_pessoal é superior a ele em sua hierarquia.");
        Usuario usuario = umUsuarioComLoopNaHierarquia();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloProprioUsuarioFicarEmLoopCom2Niveis() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível adicionar o usuário ADMIN como superior,"
                + " pois o usuário INATIVO é superior a ele em sua hierarquia.");
        Usuario usuario = umUsuarioComProximoUsuarioComoSuperior();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveGerarExcessaoNaHierarquiaPeloUsuarioSerSeuSuperior() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Não é possível atrelar o próprio usuário em sua Hierarquia.");
        Usuario usuario = umUsuarioComProprioUsuarioComoSuperior();
        service.hierarquiaIsValida(usuario);

    }

    @Test
    public void deveEditarHierarquiaSemExceptions() {
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

    @Test
    public void save_cidadesAdicionadas_quandoAdicionarNovasCidadesEManterACidadeExistente() {
        var usuario = service.findByIdCompleto(100);
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 3237, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 1443, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 2466, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 3022, 100));
        service.save(usuario);
        var usuarioComNovasCidades = service.findByIdCompleto(100);
        assertThat(usuarioComNovasCidades.getCidades())
            .hasSize(5)
            .extracting("usuario.id", "cidade.id")
            .containsExactlyInAnyOrder(
                tuple(100, 5578),
                tuple(100, 3237),
                tuple(100, 1443),
                tuple(100, 2466),
                tuple(100, 3022));

        assertThat(usuarioComNovasCidades.getHistoricos()).isNotNull();
        assertThat(usuarioComNovasCidades.getHistoricos())
            .extracting("situacao", "observacao")
            .containsAnyOf(
                tuple(ESituacao.A, "Alteração nos dados de cadastro do usuário.")
            );
    }

    @Test
    public void updateFromQueue_deveEnviarParaFilaDeCadastroDeUsuario_quandoSalvarUsuarioCorretamente() {
        service.updateFromQueue(umUsuario());
        verify(sender, times(0)).sendSuccess(any(UsuarioDto.class));
    }

    @Test
    public void save_cidadesAdicionadasERemovidas_quandoAdicionarNovasCidadesERemoverACidadeExistente() {
        var usuario = service.findByIdCompleto(100);
        usuario.setCidades(Sets.newHashSet(
                Arrays.asList(
                        UsuarioCidade.criar(usuario, 3237, 100),
                        UsuarioCidade.criar(usuario, 1443, 100),
                        UsuarioCidade.criar(usuario, 2466, 100),
                        UsuarioCidade.criar(usuario, 3022, 100))));
        service.save(usuario);
        var usuarioComCidadesAtualizadas = service.findByIdCompleto(100);
        assertThat(usuarioComCidadesAtualizadas.getCidades())
                .hasSize(4)
                .extracting("usuario.id", "cidade.id")
                .containsExactlyInAnyOrder(
                        tuple(100, 3237),
                        tuple(100, 1443),
                        tuple(100, 2466),
                        tuple(100, 3022));

        assertThat(usuarioComCidadesAtualizadas.getHistoricos()).isNotNull();
        assertThat(usuarioComCidadesAtualizadas.getHistoricos())
            .extracting("situacao", "observacao")
            .containsAnyOf(
                tuple(ESituacao.A, "Alteração nos dados de cadastro do usuário.")
            );
    }

    @Test
    public void save_cidadesRemovidas_quandoRemoverAsCidadesExistentes() {
        var usuario = service.findByIdCompleto(100);
        usuario.setCidades(Sets.newHashSet());
        service.save(usuario);
        var usuarioComCidadesRemovidas = service.findByIdCompleto(100);
        assertThat(usuarioComCidadesRemovidas.getCidades()).isEmpty();
        assertThat(usuarioComCidadesRemovidas.getHistoricos()).isNotNull();
        assertThat(usuarioComCidadesRemovidas.getHistoricos())
            .extracting("situacao", "observacao")
            .containsAnyOf(
                tuple(ESituacao.A, "Alteração nos dados de cadastro do usuário.")
            );
    }

    @Test
    public void save_cidadesNaoAlteradas_quandoAdicionarUmaCidadeJaExistente() {
        var usuario = service.findByIdCompleto(100);
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 5578, 100));
        service.save(usuario);
        var usuarioAtualizado = service.findByIdCompleto(100);
        assertThat(usuarioAtualizado.getCidades())
                .hasSize(1)
                .extracting("usuario.id", "cidade.id")
                .containsExactlyInAnyOrder(
                        tuple(100, 5578));

        assertThat(usuarioAtualizado.getHistoricos()).isNotNull();
        assertThat(usuarioAtualizado.getHistoricos())
            .extracting("situacao", "observacao")
            .containsAnyOf(
                tuple(ESituacao.A, "Alteração nos dados de cadastro do usuário.")
            );
    }

    @Test
    public void save_cidadesAdicionadas_quandoAdicionarCidadesAUmUsuarioSemCidades() {
        var usuario = service.findByIdCompleto(101);
        assertThat(usuario.getCidades()).isEmpty();
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 3237, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 1443, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 2466, 100));
        usuario.adicionarCidade(UsuarioCidade.criar(usuario, 3022, 100));
        service.save(usuario);
        var usuarioComNovasCidades = service.findByIdCompleto(101);
        assertThat(usuarioComNovasCidades.getCidades())
                .hasSize(4)
                .extracting("usuario.id", "cidade.id")
                .containsExactlyInAnyOrder(
                        tuple(101, 3237),
                        tuple(101, 1443),
                        tuple(101, 2466),
                        tuple(101, 3022));

        assertThat(usuarioComNovasCidades.getHistoricos()).isNotNull();
        assertThat(usuarioComNovasCidades.getHistoricos())
            .extracting("situacao", "observacao")
            .containsAnyOf(
                tuple(ESituacao.A, "Alteração nos dados de cadastro do usuário.")
            );
    }

    @Test
    public void getSuperioresDoUsuario_deveRetornar_quandoPossuirSuperiores() {
        assertThat(service.getSuperioresDoUsuario(110)).hasSize(2).extracting("id").containsExactly(112, 113);
    }

    @Test
    public void getSuperioresDoUsuario_deveRetornarVazio_quandoNaoPossuirSuperiores() {
        assertThat(service.getSuperioresDoUsuario(123)).isEmpty();
    }

    @Test
    public void getSuperioresDoUsuario_deveRetornarVazio_quandoNaoExistirUsuario() {
        assertThat(service.getSuperioresDoUsuario(121)).isEmpty();
    }

    @Test
    public void getSuperioresDoUsuarioPorCargo_deveRetornar_quandoPossuirSuperiores() {
        assertThat(service.getSuperioresDoUsuarioPorCargo(110, CodigoCargo.ADMINISTRADOR))
                .hasSize(2).extracting("id").containsExactly(112, 113);
    }

    @Test
    public void getSuperioresDoUsuarioPorCargo_deveRetornarVazio_quandoNaoPossuirSuperioresComEsseCargo() {
        assertThat(service.getSuperioresDoUsuarioPorCargo(120, EXECUTIVO)).isEmpty();
    }

    @Test
    public void getSuperioresDoUsuarioPorCargo_deveRetornarVazio_quandoNaoExistirUsuario() {
        assertThat(service.getSuperioresDoUsuario(121)).isEmpty();
    }

    @Test
    public void getUsuariosSupervisoresDoAaAutoComplete_deveRetornarCoordenadoresGerentes_quandoEstiverAtivo() {
        var usuarioLogado = umUsuarioAutenticado();
        usuarioLogado.setCargoCodigo(EXECUTIVO);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioLogado);
        assertThat(
            service.getUsuariosSupervisoresDoAaAutoComplete(149))
            .hasSize(2)
            .extracting("id", "nome", "email", "cargo")
            .containsExactly(
                Assertions.tuple(150, "USUARIO DE COORDENADOR", "MARIA@NET2.COM", "Coordenador"),
                Assertions.tuple(151, "USUARIO DE GERENTE", "LUISFLORIDO@XBRAIN2.COM.BR", "Gerente"));
    }

    @Test
    public void getUsuariosSuperioresAutoComplete_deveRetornarListaVazia_quandoUsuarioNaoTiverCoordenadorGerente() {
        var usuarioLogado = umUsuarioAutenticado();
        usuarioLogado.setCargoCodigo(EXECUTIVO);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioLogado);
        assertThat(
            service.getUsuariosSupervisoresDoAaAutoComplete(119))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void getUsuariosInativosByIds_retornarUsuriosInativos_quandoForPassadoIds() {
        assertThat(service.getUsuariosInativosByIds(List.of(100, 101, 105, 370)))
                .hasSize(2).extracting("id", "situacao")
                .contains(tuple(105, ESituacao.I), tuple(370, ESituacao.I));
    }

    @Test
    public void buscarColaboradoresAtivosOperacaoComericialPorCargo_deveBuscarPorCargo_quandoInformadoPorId() {
        assertThat(service.buscarColaboradoresAtivosOperacaoComericialPorCargo(5))
            .extracting("id", "nome", "email", "nomeCargo", "codigoCargo")
            .containsExactlyInAnyOrder(
                tuple(116, "ALBERTO PEREIRA", "ALBERTO@NET.COM", "Executivo", EXECUTIVO),
                tuple(149, "USUARIO INFERIOR", "MARIA@NET3.COM", "Executivo", EXECUTIVO),
                tuple(117, "ROBERTO ALMEIDA", "ROBERTO@NET.COM", "Executivo", EXECUTIVO),
                tuple(998, "USUARIO REMANEJAR", "MARIA@NET3.COM", "Executivo", EXECUTIVO),
                tuple(1000, "USUARIO REMANEJAR", "MARIA@NET3.COM", "Executivo", EXECUTIVO)
            );
    }

    @Test
    public void validarUsuarioComCpfDiferenteRemanejado_deveLancarException_quandoJaHouverUmUsuarioComCpfNaoRemanejado() {
        var usuarioMqRequest = umUsuarioRemanejamento();
        usuarioMqRequest.setId(999);
        usuarioMqRequest.setCpf("87458480092");
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.validarUsuarioComCpfDiferenteRemanejado(Usuario.parse(usuarioMqRequest)))
            .withMessage("Não é possível remanejar o usuário pois já existe outro usuário para este CPF.");
    }

    @Test
    public void remanejarUsuario_deveRemanejarAntigoEDuplicarCriandoUmNovo_quandoDadosEstiveremCorretos() {
        var usuarioMqRequest = umUsuarioRemanejamento();

        var usuariosAntesRemanejar = usuarioRepository.findAllByCpf(usuarioMqRequest.getCpf());

        assertThat(usuariosAntesRemanejar)
            .extracting("id", "situacao")
            .containsExactly(tuple(1000, ESituacao.A));

        service.remanejarUsuario(usuarioMqRequest);

        var usuariosAposRemanejar = usuarioRepository.findAllByCpf(usuarioMqRequest.getCpf());

        assertThat(usuariosAposRemanejar)
            .extracting("id", "situacao")
            .containsAnyOf(tuple(1000, ESituacao.R));

        verify(atualizarUsuarioMqSender, times(1)).sendUsuarioRemanejadoAut(any());
        verify(atualizarUsuarioMqSender, times(0)).sendErrorUsuarioRemanejadoAut(any());
        verify(feederService, times(1)).adicionarPermissaoFeederParaUsuarioNovo(any(), any());
    }

    @Test
    public void remanejarUsuario_deveRemoverFormatacaoCpf_quandoEnviarParaRemanejar() {
        var usuarioMqRequest = umUsuarioRemanejamento();
        var cpfFormatado = "955.125.930-05";
        usuarioMqRequest.setCpf(cpfFormatado);
        service.remanejarUsuario(usuarioMqRequest);

        var usuarioRemanejado = usuarioRepository.findAllByCpf(umUsuarioRemanejamento().getCpf());

        assertThat(usuarioRemanejado)
            .extracting("situacao", "cpf")
            .containsExactly(tuple(ESituacao.A, "95512593005"), tuple(ESituacao.R, "95512593005"));

        verify(atualizarUsuarioMqSender, times(1)).sendUsuarioRemanejadoAut(any());
        verify(atualizarUsuarioMqSender, times(0)).sendErrorUsuarioRemanejadoAut(any());
        verify(feederService, times(1)).adicionarPermissaoFeederParaUsuarioNovo(any(), any());
    }

    @Test
    public void alterarDadosAcessoEmail_deveAlterarEmailEEnviarParaFila_quandoDadosEstiveremCorretos() {
        service.alterarDadosAcessoEmail(umUsuarioDadosAcessoRequest());
        verify(sender, times(1)).sendSuccess(any());
    }

    private UsuarioDadosAcessoRequest umUsuarioDadosAcessoRequest() {
        return UsuarioDadosAcessoRequest
            .builder()
            .usuarioId(104)
            .alterarSenha(Eboolean.F)
            .emailAtual("operacao_gerente_comercial@net.com.br")
            .emailNovo("NOVO@EMAIL.COM")
            .ignorarSenhaAtual(true)
            .build();
    }

    @Test
    public void salvarUsuarioFeeder_deveSalvarUsuarioEEnviarSenha_quandoEmailCpfNaoRegistrado() {
        service.salvarUsuarioFeeder(umUsuarioFeeder());

        assertThat(usuarioRepository.findByEmail("JOHN@GMAIL.COM").get())
            .extracting("nome", "email", "cpf", "cargoCodigo", "cargoId",
                "usuarioCadastro.id", "dataCadastro", "empresasId", "departamentoId", "nivelCodigo", "unidadesNegociosId",
                "alterarSenha")
            .containsExactlyInAnyOrder("JOHN DOE", "JOHN@GMAIL.COM", "47492951671", GERADOR_LEADS, 96, 231,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), List.of(2, 3), 68,
                CodigoNivel.FEEDER, List.of(2), Eboolean.V);

        verify(notificacaoService, times(1)).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, times(1)).sendCadastroSuccessoMensagem(any());
    }

    @Test
    public void salvarUsuarioFeeder_deveSalvarMesmoUsuarioComoUsuarioCadastro_quandoUsuarioAutocadastrado() {
        var umGeradorLeadsAutoCadastrado = umUsuarioFeeder();
        umGeradorLeadsAutoCadastrado.setUsuarioCadastroId(null);

        service.salvarUsuarioFeeder(umGeradorLeadsAutoCadastrado);

        var usuarioId = service.findByEmail("JOHN@GMAIL.COM").getId();

        assertThat(usuarioRepository.findByEmail("JOHN@GMAIL.COM").get())
            .extracting("nome", "email", "cpf", "cargoCodigo", "cargoId",
                "usuarioCadastro.id", "dataCadastro", "empresasId", "departamentoId", "nivelCodigo", "unidadesNegociosId",
                "alterarSenha")
            .containsExactlyInAnyOrder("JOHN DOE", "JOHN@GMAIL.COM", "47492951671", GERADOR_LEADS, 96, usuarioId,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), List.of(2, 3), 68,
                CodigoNivel.FEEDER, List.of(2), Eboolean.V);

        verify(notificacaoService, times(1)).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, times(1)).sendCadastroSuccessoMensagem(any());
    }

    @Test
    public void salvarUsuarioFeeder_deveSalvarUsuarioComCargoImportador_quandoTipoGeradorForImportadorCargas() {
        var umImportadorCargas = umUsuarioFeeder();
        umImportadorCargas.setUsuarioCadastroId(null);
        umImportadorCargas.setTipoGerador(IMPORTADOR_CARGAS);

        service.salvarUsuarioFeeder(umImportadorCargas);

        var usuarioId = service.findByEmail("JOHN@GMAIL.COM").getId();

        assertThat(usuarioRepository.findByEmail("JOHN@GMAIL.COM").get())
            .extracting("nome", "email", "cpf", "cargoCodigo", "cargoId",
                "usuarioCadastro.id", "dataCadastro", "empresasId", "departamentoId", "nivelCodigo", "unidadesNegociosId",
                "alterarSenha")
            .containsExactlyInAnyOrder("JOHN DOE", "JOHN@GMAIL.COM", "47492951671", IMPORTADOR_CARGAS, 97, usuarioId,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), List.of(2, 3), 68,
                CodigoNivel.FEEDER, List.of(2), Eboolean.V);

        verify(notificacaoService, times(1)).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, times(1)).sendCadastroSuccessoMensagem(any());
    }

    @Test
    public void salvarUsuarioFeeder_deveDarErro_quandoCpfRegistrado() {
        var umGeradorLeadsComCpfExistente = umUsuarioFeeder();
        umGeradorLeadsComCpfExistente.setCpf("75952969874");

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioFeeder(umGeradorLeadsComCpfExistente))
            .withMessageContaining("CPF já cadastrado.");

        verify(notificacaoService, never()).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, never()).sendCadastroSuccessoMensagem(any());
    }

    @Test
    public void salvarUsuarioFeeder_deveDarErro_quandoEmailRegistrado() {
        var umGeradorLeadsComEmailExistente = umUsuarioFeeder();
        umGeradorLeadsComEmailExistente.setEmail("USUARIO_TESTE@GMAIL.COM");

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioFeeder(umGeradorLeadsComEmailExistente))
            .withMessageContaining("Email já cadastrado.");

        verify(notificacaoService, never()).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, never()).sendCadastroSuccessoMensagem(any());
    }

    @Test
    public void salvarUsuarioFeeder_deveAlterarDadosENaoEnviarEmail_quandoUsuarioCadastrado() {
        service.salvarUsuarioFeeder(umUsuarioFeeder());
        var usuarioId = service.findByEmail("JOHN@GMAIL.COM").getId();
        var geradorLeadsAlterado = umUsuarioFeeder();
        geradorLeadsAlterado.setUsuarioId(usuarioId);
        geradorLeadsAlterado.setEmail("JONNY@GMAIL.COM");

        service.salvarUsuarioFeeder(geradorLeadsAlterado);

        assertThat(service.findByIdCompleto(usuarioId))
            .extracting("nome", "email", "cpf", "cargoCodigo", "cargoId",
                "usuarioCadastro.id", "dataCadastro", "empresasId", "departamentoId", "nivelCodigo", "unidadesNegociosId",
                "alterarSenha")
            .containsExactlyInAnyOrder("JOHN DOE", "JONNY@GMAIL.COM", "47492951671", GERADOR_LEADS, 96, 231,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), List.of(2, 3), 68,
                CodigoNivel.FEEDER, List.of(2), Eboolean.V);

        verify(notificacaoService, times(1)).enviarEmailDadosDeAcesso(any(), any());
        verify(usuarioFeederCadastroSucessoMqSender, times(1)).sendCadastroSuccessoMensagem(any());
    }

    private UsuarioMqRequest umUsuarioTrocaCpf() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(104);
        usuarioMqRequest.setCpf("21145664523");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.HELP_DESK);
        usuarioMqRequest.setSituacao(ESituacao.A);
        return usuarioMqRequest;
    }

    private UsuarioMqRequest umUsuarioRemanejamento() {
        return UsuarioMqRequest
            .builder()
            .id(1000)
            .nome("USUARIO REMANEJAR")
            .email("MARIA@NET3.COM")
            .cpf("95512593005")
            .cargo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .departamento(CodigoDepartamento.AGENTE_AUTORIZADO)
            .nivel(AGENTE_AUTORIZADO)
            .unidadesNegocio(List.of(CLARO_RESIDENCIAL))
            .empresa(List.of(CLARO_TV))
            .build();
    }

    private UsuarioMqRequest umUsuarioInativo() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        usuarioMqRequest.setId(105);
        usuarioMqRequest.setCpf("41842888803");
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.HELP_DESK);
        usuarioMqRequest.setSituacao(ESituacao.I);
        return usuarioMqRequest;
    }

    private Usuario umUsuarioGerente() {
        var usuario = usuarioRepository.findOne(227);
        usuario.setCargo(cargoRepository.findByCodigo(CodigoCargo.GERENTE_OPERACAO));
        return usuario;
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
        return usuarioMqRequest;
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado
            .builder()
            .id(1)
            .nome("USUARIO")
            .email("USUARIO@TESTE.COM")
            .cargoCodigo(GERENTE_OPERACAO)
            .nivelCodigo(OPERACAO.name())
            .build();
    }

    private UsuarioFeederMqDto umUsuarioFeeder() {
        return UsuarioFeederMqDto.builder()
            .cpf("47492951671")
            .dataCadastro(LocalDateTime.of(2020,1, 29, 11, 11, 11))
            .email("JOHN@GMAIL.COM")
            .geradorLeadsId(101)
            .telefone("998230087")
            .situacao(ESituacao.A)
            .nome("JOHN DOE")
            .tipoGerador(GERADOR_LEADS)
            .usuarioCadastroId(231)
            .build();
    }

    @Test
    public void buscarUsuariosAtivosNivelOperacao_deveRetornarAtivosOperacao_quandoCanalDoUsuarioForAgenteAutorizado() {
        assertThat(usuarioService.buscarUsuariosAtivosNivelOperacaoCanalAa())
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(239,"VENDEDOR OPERACAO 2 - ( Pessoal )"),
                tuple(240,"VENDEDOR OPERACAO 3 - ( Pessoal )")
            );
    }

    @Test
    public void buscarAtualByCpf_deveRetornarUsuarioAtual_quandoInformarCpf() {
        var usuario = usuarioService.buscarAtualByCpf("38957979875");

        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(100);
        assertThat(usuario.getCpf()).isEqualTo("38957979875");
        assertThat(usuario.getEmail()).isEqualTo("ADMIN@XBRAIN.COM.BR");
        assertThat(usuario.getSituacao()).isEqualTo(ESituacao.A);
    }

    @Test
    public void buscarAtualByCpf_deveRetornarException_quandoNaoEncontrarUsuario() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.buscarAtualByCpf("123456789"))
            .withMessage("O usuário não foi encontrado.");
    }

    @Test
    public void findAtualByEmail_deveRetornarUsuarioAtual_quandoInformarEmail() {
        var usuario = usuarioService.buscarAtualByEmail("ADMIN@XBRAIN.COM.BR");

        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(100);
        assertThat(usuario.getCpf()).isEqualTo("38957979875");
        assertThat(usuario.getEmail()).isEqualTo("ADMIN@XBRAIN.COM.BR");
        assertThat(usuario.getSituacao()).isEqualTo(ESituacao.A);
    }

    @Test
    public void findAtualByEmail_deveRetornarException_quandoNaoEncontrarUsuario() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.buscarAtualByEmail("EMAILNAOEXISTENTE@EMAIL.COM"))
            .withMessage("O usuário não foi encontrado.");
    }

    @Test
    public void inativarPorAgenteAutorizado_deveInativarUsuarioEGerarHistorico_quandoInformarId() {
        var usuarioAtivo = usuarioRepository.findById(100).get();
        assertThat(usuarioAtivo.isAtivo()).isTrue();

        service.inativarPorAgenteAutorizado(new UsuarioDto(usuarioAtivo.getId()));

        var usuarioInativo = usuarioRepository.findById(100).get();

        assertThat(usuarioInativo.isAtivo()).isFalse();

        assertThat(usuarioHistoricoRepository.findByUsuarioId(usuarioInativo.getId()))
            .extracting("motivoInativacao.codigo", "observacao", "situacao")
            .contains(tuple(CodigoMotivoInativacao.DEMISSAO, "Inativado pelo Agente Autorizado.", ESituacao.I));

        verify(autenticacaoService, times(1)).logout(anyInt());
    }

    @Test
    public void inativarPorAgenteAutorizado_deveNaoOcorrerNada_quandoUsuarioNaoEstiverAtivo() {
        service.inativarPorAgenteAutorizado((new UsuarioDto(12316)));

        verify(autenticacaoService, times(0)).logout(anyInt());
    }
}
