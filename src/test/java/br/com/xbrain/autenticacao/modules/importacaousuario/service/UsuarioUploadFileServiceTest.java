package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil.CELULA_NACIMENTO;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil.CELULA_NOME;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargoReceptivo;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoComercial;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.umNivelAa;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.umNivelReceptivo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioUploadFileServiceTest {

    @InjectMocks
    private UsuarioUploadFileService usuarioUploadFileService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private NivelRepository nivelRepository;
    @Mock
    private DepartamentoRepository departamentoRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificacaoService notificacaoService;

    private Sheet sheet;

    @Before
    @SneakyThrows
    public void setUp() {
        var excelFile = new FileInputStream("src/test/resources/arquivo_usuario/planilha.xlsx");
        var wb = new XSSFWorkbook(excelFile);
        sheet = wb.getSheetAt(0);
    }

    @Test
    public void processarUsuarios_deveProcessarRowERetornarErro_seCpfInvalido() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelAa());

        var row = umaLinha(2);

        var usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, new UsuarioImportacaoRequest(true, false));

        assertNotNull(usuarioImportacaoRequest);
        assertThat(usuarioImportacaoRequest.getCpf()).isEmpty();
        assertThat(usuarioImportacaoRequest.getMotivoNaoImportacao()).contains("O campo cpf está incorreto.");
    }

    @Test
    public void processarUsuarios_deveProcessarRowERetornarErro_seNivelInvalido() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelAa());

        var row = umaLinha(2);

        var usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, new UsuarioImportacaoRequest(true, false));
        assertNotNull(usuarioImportacaoRequest);
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().get(0),
                "O nível AGENTE_AUTORIZADO não é possível importar via arquivo.");
    }

    @Test
    public void validarCpf_deveRetornarErro_seCpfInvalido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .cpf("96124736334")
            .build();

        var msgErro = usuarioUploadFileService.validarCpf(usuarioImportacaoRequest);
        assertEquals("O campo cpf está incorreto.", msgErro);
    }

    @Test
    public void validarCpf_deveRetornarNenhumErro_seCpfValido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .cpf("38957979875")
            .build();

        var msgErro = usuarioUploadFileService.validarCpf(usuarioImportacaoRequest);
        assertNotEquals("O campo cpf está incorreto.", msgErro);
    }

    @Test
    public void validarEmail_deveRetornarErro_seEmailInvalido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .email("ADMINTESTE.XBRAIN.COM.BR")
            .build();

        var msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertEquals("O campo email está inválido.", msgErro);
    }

    @Test
    public void validarEmail_deveRetornarErro_seEmailComQuantidadeDeCaracteresInvalido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .email("Beatriz_Laura_Maria_Júlia_Ana_Alice_Sofia_Maria_Eduarda_Larissa"
                + "_Mariana_Isabela_Camila_Valentina_Lara_Letícia_Helena_Amanda_"
                + "Luana_Yasmin@Mail.com")
            .build();

        var msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertEquals("O campo email está inválido.", msgErro);
    }

    @Test
    public void validarEmail_deveRetornarNenhumErro_seEmailValido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .email("ADMINTESTE@XBRAIN.COM.BR")
            .build();

        var msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertNotEquals("O campo email está incorreto.", msgErro);
    }

    @Test
    public void validarUsuarioExistente_deveRetornarErro_seCpfJaExistente() {
        when(usuarioRepository.findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(any(), any(), any()))
            .thenReturn(List.of(new Usuario()));

        var usuario = UsuarioImportacaoPlanilha.builder()
            .cpf("38957979875")
            .build();

        var msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuario, false);
        assertEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void validarUsuarioExistente_deveRetornarNenhumErro_seCpfNaoExistente() {
        var usuario = UsuarioImportacaoPlanilha.builder()
            .cpf("9612473633")
            .build();

        var msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuario, false);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void validarUsuarioExistente_deveRetornarErro_seEmailJaExistaIndiferenteDoCase() {
        when(usuarioRepository.findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(any(), any(), any()))
            .thenReturn(List.of(new Usuario()));

        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .email("ADMIN@XBRAIN.COM.BR")
            .build();

        var msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest, false);
        assertEquals("Usuário já salvo no banco", msgErro);

        usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMIN@XBRAIN.COM.BR".toLowerCase())
                .build();

        msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest, false);
        assertEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void validarUsuarioExistente_deveRetornarNenhumErro_seEmailNaoExistente() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .email("ADMINTeste@XBRAIN.COM.BR")
            .build();

        var msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest, false);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void validarNome_deveRetornarErro_seQuantidadeDeCaracteresAcimaDoPermitido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .nome("Beatriz Laura Maria Júlia Ana Alice Sofia Maria Eduarda Larissa "
                + "Mariana Isabela Camila Valentina Lara Letícia Helena Amanda Luana Yasmin")
            .build();

        var msgErro = usuarioUploadFileService.validarNome(usuarioImportacaoRequest);
        assertEquals("Usuário está com nome inválido", msgErro);
    }

    @Test
    public void validarNome_deveRetornarNenhumErro_seQuantidadeDeCaracteresMenorOuIgualAoPermitido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .nome("Beatriz Laura Maria Júlia Ana Alice Sofia Maria Eduarda Larissa "
                + "Mariana Isabela Camila Valentina Lar")
            .build();

        var msgErro = usuarioUploadFileService.validarNome(usuarioImportacaoRequest);
        assertNotEquals("Usuário está com nome inválido", msgErro);
    }

    @Test
    public void validarNascimento_deveRetornarErro_seDataNascimentoInvalida() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .nascimento(null)
            .build();

        var msgErro = usuarioUploadFileService.validarNascimento(usuarioImportacaoRequest);
        assertEquals("Usuário está com nascimento inválido", msgErro);
    }

    @Test
    public void validarNascimento_naoDeveRetornarErro_seDataNascimentoValida() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha.builder()
            .nascimento(LocalDateTime.now().minusYears(20L))
            .build();

        var msgErro = usuarioUploadFileService.validarNascimento(usuarioImportacaoRequest);
        assertNotEquals("Usuário está com nascimento inválido", msgErro);
    }

    @Test
    public void buildUsuario_cargoDeveEstarNulo_quandoNomeDoNivelEstiverIncorreto() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));

        assertThat(usuarioUploadFileService.buildUsuario(umaLinha(3), "102030", false))
            .extracting("nome", "cpf", "email", "telefone", "situacao", "cargo.nome")
            .contains("ADRIANA DE LIMA BEZERRA", "70159931479", "n.adriana.lbezerra@aec.com.br",
                "51991301817", ESituacao.A, "Vendedor Receptivo");
    }

    @Test
    public void buildUsuario_deveTratarCpf_quandoPossuirSimbolos() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());

        var usuario = usuarioUploadFileService
                .buildUsuario(umaLinha(3), "102030", false);

        assertThat(usuario.getCpf()).isEqualTo("70159931479");
    }

    @Test
    public void buildUsuario_deveRetornarMotivoNaoImportacao_quandoNomeDoUsuarioVazio() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));
        var linha = umaLinha(3);
        linha.getCell(CELULA_NOME).setCellValue("");

        var result = usuarioUploadFileService.buildUsuario(linha, "102030", false);

        assertThat(result.getMotivoNaoImportacao())
            .isEqualTo(List.of(
                "Usuário está com nome inválido",
                "Usuário está com departamento inválido"));

        assertThat(result)
            .extracting("nome", "cpf", "email", "telefone", "situacao", "cargo.nome")
            .contains("", "70159931479", "n.adriana.lbezerra@aec.com.br",
                "51991301817", ESituacao.A, "Vendedor Receptivo");
    }

    @Test
    public void buildUsuario_deveRetornarMotivoNaoImportacao_quandoNascimentoVazio() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));
        var linha = umaLinha(3);
        linha.getCell(CELULA_NACIMENTO).setCellValue("");

        var result = usuarioUploadFileService.buildUsuario(linha, "102030", false);

        assertThat(result.getMotivoNaoImportacao())
            .isEqualTo(List.of(
                "Usuário está com departamento inválido",
                "Usuário está com nascimento inválido"));

        assertThat(result)
            .extracting("nascimento", "nome", "cpf", "email", "telefone", "situacao", "cargo.nome")
            .contains("ADRIANA DE LIMA BEZERRA", "70159931479", "n.adriana.lbezerra@aec.com.br",
                "51991301817", ESituacao.A, "Vendedor Receptivo");
    }

    @Test
    public void buildUsuario_deveRetornarMotivoNaoImportacaoELogDeErro_quandoNivelNaoEncontrado() {
        doThrow(IllegalArgumentException.class)
            .when(nivelRepository)
            .findByCodigo(any());

        var logger = (Logger) getLogger(UsuarioUploadFileService.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        var result = usuarioUploadFileService.buildUsuario(umaLinha(3), "102030", false);

        assertThat(result.getMotivoNaoImportacao())
            .isEqualTo(List.of(
                "Falha ao recuperar cargo/nível",
                "Usuário está com departamento inválido",
                "Usuário está com cargo inválido"));

        assertThat(result)
            .extracting("nivel", "cargo", "nome", "cpf", "email")
            .contains(null, null, "ADRIANA DE LIMA BEZERRA", "70159931479", "n.adriana.lbezerra@aec.com.br");

        assertEquals("Erro ao recuperar nivel.",
            listAppender.list.get(0).getMessage());

        verify(nivelRepository).findByCodigo(any());
        verify(departamentoRepository, never()).findByCodigoAndNivelId(any(), any());
    }

    @Test
    public void buildUsuario_deveRetornarMotivoNaoImportacaoELogDeErro_quandoNaoConseguirTratarData() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());

        var logger = (Logger) getLogger(UsuarioUploadFileService.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        var linha = umaLinha(3);
        linha.getCell(CELULA_NACIMENTO).setCellValue("");

        var result = usuarioUploadFileService.buildUsuario(linha, "102030", false);

        assertThat(result.getMotivoNaoImportacao())
            .isEqualTo(List.of(
                "Usuário está com departamento inválido",
                "Usuário está com cargo inválido",
                "Usuário está com nascimento inválido"));

        assertThat(result)
            .extracting("nome", "cpf", "email")
            .contains("ADRIANA DE LIMA BEZERRA", "70159931479", "n.adriana.lbezerra@aec.com.br");

        assertEquals("Erro ao tratar data.",
            listAppender.list.get(0).getMessage());

        verify(nivelRepository).findByCodigo(any());
        verify(departamentoRepository).findByCodigoAndNivelId(any(), any());
    }

    @Test
    public void validarCargo_deveRetornarErro_seCargoInvalido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .cargo(null)
                .build();
        var msgErro = usuarioUploadFileService.validarCargo(usuarioImportacaoRequest);
        assertEquals("Usuário está com cargo inválido", msgErro);
    }

    @Test
    public void validarDepartamento_deveRetornarErro_seDepartamentoInvalido() {
        var usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .departamento(null)
                .build();
        var msgErro = usuarioUploadFileService.validarDepartamento(usuarioImportacaoRequest);
        assertEquals("Usuário está com departamento inválido", msgErro);
    }

    @Test
    public void tratarSenha_deveGerarSenhaRandomica_sePassadoOParametroSenhaPadraoFalse() {
        String senha = usuarioUploadFileService.tratarSenha(false);
        assertNotEquals("102030", senha);
    }

    @Test
    public void tratarSenha_deveGerarSenhaRandomica_sePassadoOParametroSenhaPadraoTrue() {
        String senha = usuarioUploadFileService.tratarSenha(true);
        assertEquals("102030", senha);
    }

    @Test
    public void processarUsuarios_deveEnviarEmail_quandoSenhaPadraoFalse() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(departamentoRepository.findByCodigoAndNivelId(any(), any()))
            .thenReturn(Optional.of(umDepartamentoComercial()));
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(umUsuarioSalvo());

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(false, false));

        verify(notificacaoService, times(1)).enviarEmailDadosDeAcesso(eq(umUsuarioSalvo()), any());
    }

    @Test
    public void processarUsuarios_naoDeveEnviarEmail_quandoForSenhaPadrao() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(departamentoRepository.findByCodigoAndNivelId(any(), any()))
            .thenReturn(Optional.of(umDepartamentoComercial()));
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(umUsuarioSalvo());

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
            new UsuarioImportacaoRequest(true, false));

        verify(notificacaoService, never()).enviarEmailDadosDeAcesso(eq(umUsuarioSalvo()), any());
    }

    @Test
    public void processarUsuarios_naoDeveEnviarEmail_quandoUsuarioVazio() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());
        when(departamentoRepository.findByCodigoAndNivelId(any(), any()))
            .thenReturn(Optional.of(umDepartamentoComercial()));
        when(cargoRepository.findFirstByNomeIgnoreCaseAndNivelId(anyString(), anyInt()))
            .thenReturn(Optional.of(umCargoReceptivo()));

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
            new UsuarioImportacaoRequest(false, false));

        verify(notificacaoService, never()).enviarEmailDadosDeAcesso(eq(umUsuarioSalvo()), any());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    public void processarUsuarios_naoDeveEnviarEmail_quandoSenhaPadraoTrue() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(true, false));
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    public void processarUsuarios_naoDeveResetarSenha_quandoResetarSenhaForFalse() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());

        when(usuarioRepository.findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(any(), any(), any()))
            .thenReturn(List.of(new Usuario()));

        var usuarioImportacaoPlanilha = usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(false, false));
        assertEquals(usuarioImportacaoPlanilha.getMotivoNaoImportacao().get(0), "Usuário já salvo no banco");
    }

    @Test
    public void processarUsuarios_deveResetarSenha_quandoResetarSenhaForTrue() {
        when(nivelRepository.findByCodigo(any())).thenReturn(umNivelReceptivo());

        when(usuarioRepository.findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(any(), any(), any()))
            .thenReturn(List.of(new Usuario()));

        var usuarioImportacaoPlanilha = usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(true, true));
        assertEquals(usuarioImportacaoPlanilha.getMotivoNaoImportacao().get(0), "Usuário já salvo no banco,"
                + " sua senha foi resetada para a padrão.");
    }

    private Row umaLinha(int index) {
        return PlanilhaService.converterTipoCelulaParaString(sheet.getRow(index));
    }

    private static Usuario umUsuarioSalvo() {
        return Usuario.builder()
            .id(1)
            .nome("ALAN DAVISON BARROS SILVA")
            .email("n.alan.bsilva@aec.com.br")
            .telefone("51991301817")
            .cpf("2917600403")
            .nascimento(LocalDate.of(1975, 10, 11).atStartOfDay())
            .unidadesNegocios(List.of(new UnidadeNegocio(1), new UnidadeNegocio(2)))
            .empresas(List.of(new Empresa(1), new Empresa(2), new Empresa(3)))
            .cargo(umCargoReceptivo())
            .departamento(umDepartamentoComercial())
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$z8XJ8Bekzr35Wv0Ra.otKOb/TtC8563jiDzX62VDutAn4rpbyZmkC")
            .alterarSenha(Eboolean.V)
            .recuperarSenhaTentativa(0)
            .situacao(ESituacao.A)
            .build();
    }
}
