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
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Sets;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.EXECUTIVO;
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
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql"})
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
    private EquipeVendaClient equipeVendaClient;
    @MockBean
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @MockBean
    private UsuarioFeriasService usuarioFeriasService;

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
        assertEquals(usuarioDto.getCpf(), usuarioMqRequest.getCpf());
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
    public void inativar_deveInativarUmUsuario_seAtivo() {
        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(100);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        Usuario usuario = service.findByIdCompleto(100);
        assertEquals(usuario.getSituacao(), ESituacao.I);
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
        verify(equipeVendaMqSender, never()).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoSupervisor() {
        doReturn(umUsuarioSupervisor()).when(service).findComplete(205);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(205);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, atLeastOnce()).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoAssistente() {
        doReturn(umUsuarioAssistente()).when(service).findComplete(204);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(204);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, atLeastOnce()).sendInativar(any());
    }

    @Test
    public void inativar_deveEnviarParaInativarNoEquipeVendas_sePossuirCargoVendedorD2d() {
        doReturn(umUsuarioVendedorD2d()).when(service).findComplete(203);

        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(203);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        verify(equipeVendaMqSender, atLeastOnce()).sendInativar(any());
    }

    @Test
    public void salvarUsuarioRealocado_deveRealocarUsuario_quandoUsuarioEstiverAtivo() {
        Usuario usuarioRealocar = new Usuario();
        usuarioRealocar.setId(366);
        service.salvarUsuarioRealocado(usuarioRealocar);
        assertEquals(ESituacao.R, usuarioRepository.findById(usuarioRealocar.getId()).get().getSituacao());
    }

    @Test
    public void updateFromQueue_deveCriarNovoUsuario_quandoAntigoRealocado() {
        UsuarioMqRequest usuarioMqRequest = umUsuarioARealocar();
        usuarioMqRequest.setId(368);
        service.updateFromQueue(usuarioMqRequest);
        usuarioRepository.findAllByCpf("21145664523")
                .forEach(usuario -> {
                    if (usuario.getSituacao().equals(ESituacao.A)) {
                        assertEquals(ESituacao.A, usuario.getSituacao());
                    } else if (usuario.getSituacao().equals(ESituacao.R)) {
                        assertEquals(ESituacao.R, usuario.getSituacao());
                    }
                });
        assertEquals(2, usuarioRepository.findAllByCpf("21145664523").size());
    }

    @Test
    public void updateFromQueue_deveAlterarCpf_seNovoCpfValido() throws Exception {
        UsuarioMqRequest usuarioMqRequest = umUsuarioARealocar();
        usuarioMqRequest.setId(368);
        usuarioMqRequest.setCpf("43185104099");
        service.updateFromQueue(usuarioMqRequest);
        Usuario usuario = usuarioRepository
                .findTop1UsuarioByCpf("43185104099").orElseThrow(() -> new ValidacaoException("Usuário não encontrado"));
        Assert.assertNotNull(usuario);
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
    public void updateFromQueue_naoRealocaUsuario_quandoSituacaoForInativa() {
        service.updateFromQueue(umUsuarioInativo());
        List<Usuario> usuarios = usuarioRepository.findAllByCpf("41842888803");
        assertEquals(ESituacao.I, usuarios.get(0).getSituacao());
        assertEquals(1, usuarios.size());
    }

    @Test
    public void updateFromQueue_naoRealocaUsuario_quandoAFlagRealocadoForFalse() {
        UsuarioMqRequest naoRealocar = umUsuarioARealocar();
        naoRealocar.setRealocado(false);
        service.updateFromQueue(naoRealocar);
        List<Usuario> usuarios = usuarioRepository.findAllByCpf("21145664523");
        assertThat(usuarios)
                .extracting(Usuario::getSituacao)
                .containsOnly(ESituacao.A);
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


    // @Test TODO foi desativado e será refeito conforme task #13110
    public void inativarUsuariosSemAcesso_doisUsuariosInativados_quandoUsuarioNaoEfetuarLoginNosUltimosTrintaEDoisDias() {
        service.inativarUsuariosSemAcesso();

        Usuario usuarioInativo = service.findByIdCompleto(101);
        assertThat(usuarioHistoricoService.getHistoricoDoUsuario(usuarioInativo.getId()))
                .extracting("id", "motivo", "observacao")
                .contains(tuple(104, "INATIVIDADE DE ACESSO", "Inativado por falta de acesso"));

        assertEquals(ESituacao.I, usuarioInativo.getSituacao());
        assertEquals(ESituacao.I, service.findByIdCompleto(104).getSituacao());
        assertEquals(ESituacao.A, service.findByIdCompleto(100).getSituacao());
        assertEquals(0, service.getUsuariosSemAcesso().size());
        verify(inativarColaboradorMqSender, times(2)).sendSuccess(anyString());
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
    }

    @Test
    public void save_cidadesRemovidas_quandoRemoverAsCidadesExistentes() {
        var usuario = service.findByIdCompleto(100);
        usuario.setCidades(Sets.newHashSet());
        service.save(usuario);
        var usuarioComCidadesRemovidas = service.findByIdCompleto(100);
        assertThat(usuarioComCidadesRemovidas.getCidades()).isEmpty();
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
        usuarioMqRequest.setRealocado(false);
        return usuarioMqRequest;
    }
}
