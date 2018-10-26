package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioUploadFileServiceTest {

    @Autowired
    private UsuarioUploadFileService usuarioUploadFileService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RestTemplate restTemplate;

    private Sheet sheet;

    @Before
    public void setUp() throws Exception {
        InputStream excelFile = new FileInputStream("src/test/resources/arquivo_usuario/planilha.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(excelFile);
        sheet = wb.getSheetAt(0);
    }

    @Test
    public void deveProcessarRowERetornarErroDeCpfInvalido() {
        Row row = umaLinha(2);

        UsuarioImportacaoPlanilha usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, new UsuarioImportacaoRequest(true, false));
        assertNotNull(usuarioImportacaoRequest);
        assertNotNull(usuarioImportacaoRequest.getCpf());
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().get(1),
                "O campo cpf está incorreto.");
    }

    @Test
    public void deveProcessarRowERetornarErroDeNivelInvalido() {
        Row row = umaLinha(2);

        UsuarioImportacaoPlanilha usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, new UsuarioImportacaoRequest(true, false));
        assertNotNull(usuarioImportacaoRequest);
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().get(0),
                "O nível AGENTE_AUTORIZADO não é possivel importar via arquivo.");
    }

    @Test
    public void deveRetornarErroCasoOCpfEstejaInvalido() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .cpf("96124736334")
                .build();
        String msgErro = usuarioUploadFileService.validarCpf(usuarioImportacaoRequest);
        assertEquals("O campo cpf está incorreto.", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroCasoOCpfEstejaValido() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .cpf("38957979875")
                .build();

        String msgErro = usuarioUploadFileService.validarCpf(usuarioImportacaoRequest);
        assertNotEquals("O campo cpf está incorreto.", msgErro);
    }

    @Test
    public void deveRetornarErroCasoOEmailEstejaInvalido() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMINTESTE.XBRAIN.COM.BR")
                .build();
        String msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertEquals("O campo email está inválido.", msgErro);
    }

    @Test
    public void deveRetornarErroCasoOEmailComQuantidadeDeCaracteresEstejaInvalido() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("Beatriz_Laura_Maria_Júlia_Ana_Alice_Sofia_Maria_Eduarda_Larissa"
                        + "_Mariana_Isabela_Camila_Valentina_Lara_Letícia_Helena_Amanda_"
                        + "Luana_Yasmin@Mail.com")
                .build();
        String msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertEquals("O campo email está inválido.", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroCasoOEmailEstejaValido() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMINTESTE@XBRAIN.COM.BR")
                .build();
        String msgErro = usuarioUploadFileService.validarEmail(usuarioImportacaoRequest);
        assertNotEquals("O campo email está incorreto.", msgErro);
    }

    @Test
    public void deveRetornarErroCasoOCpfJaExista() {
        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
                .builder()
                .cpf("38957979875").build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuario, false);
        assertEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroCasoOCpfNaoExista() {
        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
                .builder()
                .cpf("9612473633").build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuario, false);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveRetornarErroCasoOEmailJaExistaIndiferenteDoCase() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMIN@XBRAIN.COM.BR")
                .build();

        String msgErro = usuarioUploadFileService
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
    public void deveRetornarNenhumErroCasoOEmailNaoExista() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMINTeste@XBRAIN.COM.BR")
                .build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest, false);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveValidarONome() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .nome("Beatriz Laura Maria Júlia Ana Alice Sofia Maria Eduarda Larissa "
                        + "Mariana Isabela Camila Valentina Lara Letícia Helena Amanda Luana Yasmin")
                .build();
        String msgErro = usuarioUploadFileService.validarNome(usuarioImportacaoRequest);
        assertEquals("Usuário está com nome inválido", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroAoValidarONome() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .nome("João")
                .build();
        String msgErro = usuarioUploadFileService.validarNome(usuarioImportacaoRequest);
        assertNotEquals("Usuário está com nome inválido", msgErro);
    }

    @Test
    public void deveValidarADataDeNascimento() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .nascimento(null)
                .build();
        String msgErro = usuarioUploadFileService.validarNascimento(usuarioImportacaoRequest);
        assertEquals("Usuário está com nascimento inválido", msgErro);
    }

    @Test
    public void deveValidarADataDeNascimentoSemErro() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .nascimento(LocalDateTime.now().minusYears(20L))
                .build();
        String msgErro = usuarioUploadFileService.validarNascimento(usuarioImportacaoRequest);
        assertNotEquals("Usuário está com nascimento inválido", msgErro);
    }

    @Test
    public void deveRetornarErroCasoNaoLocalizeONivel() {
        UsuarioImportacaoPlanilha usuario = usuarioUploadFileService
                .buildUsuario(umaLinha(3), "102030", false);
        assertEquals(usuario.getMotivoNaoImportacao().get(0), "Falha ao recuperar cargo/nivel");
    }

    @Test
    public void deveRetornarErroCasoNaoLocalizeOCargo() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .cargo(null)
                .build();
        String msgErro = usuarioUploadFileService.validarCargo(usuarioImportacaoRequest);
        assertEquals("Usuário está com cargo inválido", msgErro);
    }

    @Test
    public void deveRetornarErroCasoNaoLocalizeODepartamento() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .departamento(null)
                .build();
        String msgErro = usuarioUploadFileService.validarDepartamento(usuarioImportacaoRequest);
        assertEquals("Usuário está com departamento inválido", msgErro);
    }

    @Test
    public void deveGerarSenhaRandomicaQuandoPassadoOParametroSenhaPadraoFalse() {
        String senha = usuarioUploadFileService.tratarSenha(false);
        assertNotEquals("102030", senha);
    }

    @Test
    public void deveGerarSenhaRandomicaQuandoPassadoOParametroSenhaPadraoTrue() {
        String senha = usuarioUploadFileService.tratarSenha(true);
        assertEquals("102030", senha);
    }

    @Test
    public void deveEnviarEmailQuandoSenhaPadraoFalse() {
        when(restTemplate.postForEntity(anyString(), any(), any())).then(invocationOnMock -> null);

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(false, false));

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    public void deveEnviarEmailSomenteQuandoSenhaPadraoFalse() {
        when(restTemplate.postForEntity(anyString(), any(), any())).then(invocationOnMock -> null);

        usuarioUploadFileService.processarUsuarios(umaLinha(4),
                new UsuarioImportacaoRequest(true, false));
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    public void deveResetarSenhaQuandoResetarSenhaForTrue() {
        Usuario usuario = getUsuario(366);

        assertEquals(usuario.getAlterarSenha(), Eboolean.F);
        UsuarioImportacaoPlanilha usuarioImportacaoPlanilha = usuarioUploadFileService.processarUsuarios(umaLinha(18),
                new UsuarioImportacaoRequest(false, false));
        assertEquals(usuarioImportacaoPlanilha.getMotivoNaoImportacao().get(0), "Usuário já salvo no banco");
        usuario = getUsuario(366);
        assertEquals(usuario.getAlterarSenha(), Eboolean.F);
        assertEquals(usuario.getCpf(), usuarioImportacaoPlanilha.getCpf());
    }

    @Test
    public void deveResetarSenhaSomenteQuandoResetarSenhaForTrue() {
        Usuario usuario = getUsuario(100);
        assertEquals(usuario.getAlterarSenha(), Eboolean.F);

        UsuarioImportacaoPlanilha usuarioImportacaoPlanilha = usuarioUploadFileService.processarUsuarios(umaLinha(48),
                new UsuarioImportacaoRequest(true, true));
        assertEquals(usuarioImportacaoPlanilha.getMotivoNaoImportacao().get(0), "Usuário já salvo no banco,"
                + " sua senha foi resetada para a padrão.");
        usuario = getUsuario(100);
        assertEquals(usuario.getAlterarSenha(), Eboolean.V);
        assertEquals(usuario.getEmail(), usuarioImportacaoPlanilha.getEmail());
    }

    private Usuario getUsuario(Integer id) {
        return usuarioRepository.findOne(id);
    }

    private Row umaLinha(int i) {
        return PlanilhaService.converterTipoCelulaParaString(sheet.getRow(i));
    }

}