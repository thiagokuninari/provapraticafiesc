package br.com.xbrain.autenticacao.modules.importacao;

import br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

public class UsuarioInativarPlanilha {

    private static final int COLUNA_CPF_INDEX = 4;

    @Test
    @Ignore
    public void printUpdateUsuarioInativar() throws Exception {
        getUsuariosCpf(loadPlanilha("usuarios_inativar.xlsx"))
                .forEach(cpf -> System.out.println("UPDATE USUARIO SET SITUACAO = 'I' WHERE cpf = '" + cpf + "';"));
    }

    @Test
    @Ignore
    public void printUpdateColaboradorVendasInativar() throws Exception {
        getUsuariosCpf(loadPlanilha("usuarios_inativar.xlsx"))
                .forEach(cpf -> System.out.println("UPDATE COLABORADOR_VENDAS SET FK_SITUACAO = 6 WHERE cpf = '"
                        + CpfUtil.formata(cpf) + "';"));
    }

    private Stream<String> getUsuariosCpf(XSSFSheet sheet) {
        return StreamSupport.stream(spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED), false)
                .filter(u -> u.getCell(COLUNA_CPF_INDEX) != null
                        && u.getCell(COLUNA_CPF_INDEX).getCellTypeEnum() == NUMERIC)
                .map(u -> ((XSSFCell) u.getCell(COLUNA_CPF_INDEX)).getRawValue())
                .map(CpfUtil::adicionarZerosAEsquerda);
    }

    private XSSFSheet loadPlanilha(String file) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(getFileInputStream(file));
        return workbook.getSheetAt(0);
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
                Files.readAllBytes(Paths.get(
                        getClass().getClassLoader().getResource(file)
                                .getPath())));
    }
}
