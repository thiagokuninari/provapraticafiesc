package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.util.NumeroCelulaUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;


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

    private Sheet sheet;

    @Before
    public void setUp() throws Exception {
        InputStream excelFile = new FileInputStream("src/test/resources/arquivo_usuario/planilha.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(excelFile);
        sheet = wb.getSheetAt(0);
    }

    @Test
    public void processarUsuarios() {
        Row row = PlanilhaService.converterTipoCelulaParaString(sheet.getRow(NumeroCelulaUtil.CELULA_DOIS));

        UsuarioImportacaoRequest usuarioImportacaoRequest = usuarioUploadFileService
                .processarUsuarios(row, true);
        assertNotNull(usuarioImportacaoRequest);
        assertNotNull(usuarioImportacaoRequest.getCpf());
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().size(), 1);

    }

    @Test
    public void validaCampo() {
        UsuarioImportacaoRequest usuarioImportacaoRequest = new UsuarioImportacaoRequest();
        Row row = PlanilhaService.converterTipoCelulaParaString(sheet.getRow(NumeroCelulaUtil.CELULA_DOIS));
        String validaCampo = usuarioUploadFileService
                .validaCampo(
                        row.getCell(NumeroCelulaUtil.CELULA_TRES), usuarioImportacaoRequest);
        assertEquals(validaCampo, "");
        assertEquals(usuarioImportacaoRequest.getMotivoNaoImportacao().get(0), "O campo cpf esta incorreto.");

    }

    @Test
    public void validarUsuarioExistente() {

        UsuarioImportacaoRequest usuario = new UsuarioImportacaoRequest();
        usuario = new UsuarioImportacaoRequest();
        usuario.setEmail("ADMIN@XBRAIN.COM.BR");
        assertFalse(usuarioUploadFileService
                .validarUsuarioExistente(usuario)
                .getMotivoNaoImportacao()
                .size() == 0);
        usuario = new UsuarioImportacaoRequest();
        usuario.setEmail("ADMINTESTE@XBRAIN.COM.BR");
        assertTrue(usuarioUploadFileService
                .validarUsuarioExistente(usuario)
                .getMotivoNaoImportacao()
                .size() == 0);
        usuario = new UsuarioImportacaoRequest();
        usuario.setCpf("38957979875");
        assertFalse(usuarioUploadFileService
                .validarUsuarioExistente(usuario)
                .getMotivoNaoImportacao()
                .size() == 0);
        usuario = new UsuarioImportacaoRequest();
        usuario.setCpf("9612473633");
        assertTrue(usuarioUploadFileService
                .validarUsuarioExistente(usuario)
                .getMotivoNaoImportacao()
                .size() == 0);

    }

}