package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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
import org.springframework.mock.web.MockMultipartFile;
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
    MockMultipartFile mockMultipartFile;
    @Autowired
    UsuarioUploadFileService usuarioUploadFileService;

    @MockBean
    private RestTemplate restTemplate;

    private Sheet sheet;

    @Before
    public void setUp() throws Exception {
        InputStream excelFile = new FileInputStream("src/test/resources/arquivo_usuario/planilha.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(excelFile);
        sheet = wb.getSheetAt(0);
        when(restTemplate.postForEntity(anyString(), any(), any())).then(invocationOnMock -> null);
    }

    @Test
    public void deveProcessarRowERetornarErroDeCpfInvalido() {
        Row row = PlanilhaService.converterTipoCelulaParaString(sheet.getRow(2));

        UsuarioImportacaoPlanilha usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, true);
        assertNotNull(usuarioImportacaoRequest);
        assertNotNull(usuarioImportacaoRequest.getCpf());
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().size(), 1);
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
        assertEquals("O campo email está incorreto.", msgErro);
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
                .validarUsuarioExistente(usuario);
        assertEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroCasoOCpfNaoExista() {
        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
                .builder()
                .cpf("9612473633").build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuario);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveRetornarErroCasoOEmailJaExista() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMIN@XBRAIN.COM.BR")
                .build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest);
        assertEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveRetornarNenhumErroCasoOEmailNaoExista() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .email("ADMINTeste@XBRAIN.COM.BR")
                .build();

        String msgErro = usuarioUploadFileService
                .validarUsuarioExistente(usuarioImportacaoRequest);
        assertNotEquals("Usuário já salvo no banco", msgErro);
    }

    @Test
    public void deveValidarONome() {
        UsuarioImportacaoPlanilha usuarioImportacaoRequest = UsuarioImportacaoPlanilha
                .builder()
                .nome("")
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
                .buildUsuario(PlanilhaService
                        .converterTipoCelulaParaString(sheet.getRow(3)), "102030");
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
    public void deveEnviarOsDadosDeAcessoQuandoPassadoOParametroSenhaPadraoFalse() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("Joao@mail.com");
        usuario.setNome("Joao");
        usuarioUploadFileService.notificarUsuario(usuario, "204050", false);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    public void deveEnviarOsDadosDeAcessoQuandoPassadoOParametroSenhaPadraoTrue() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("Joao@mail.com");
        usuario.setNome("Joao");
        usuarioUploadFileService.notificarUsuario(usuario, "204050", true);

        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

}