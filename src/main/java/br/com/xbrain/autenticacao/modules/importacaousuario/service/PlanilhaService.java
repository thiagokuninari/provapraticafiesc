package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PlanilhaService {

    public static boolean compararColunas(Cell cell, String valorColuna) {
        return cell != null && cell.getRichStringCellValue()
                .toString()
                .trim()
                .toUpperCase()
                .equals(valorColuna);
    }

    public Sheet getSheet(MultipartFile file) throws ValidacaoException {
        try {
            Sheet sheet = null;
            String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));

            String arquivoXlsx = ".xlsx";
            if (extensao.equals(arquivoXlsx)) {
                sheet = new XSSFWorkbook(file.getInputStream()).getSheetAt(0);
            } else {
                throw new ValidacaoException("Não foi possível reconhecer o formato do arquivo");
            }
            return sheet;
        } catch (ValidacaoException | IOException ex) {
            throw new ValidacaoException("Não foi possível recuperar o sheet");
        }
    }

    public static Row converterTipoCelulaParaString(Row linha) {
        for (int i = 0; i < NumeroCelulaUtil.QNT_COL; i++) {
            Cell cell = linha.getCell(i);
            if (cell == null) {
                if (linha.getSheet()
                        .getRow(0)
                        .getCell(i)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .equals("DATA NASCIMENTO")) {
                    linha.createCell(NumeroCelulaUtil.CELULA_NACIMENTO, CellType.NUMERIC);

                } else {
                    linha.createCell(i, CellType.STRING);
                }
            } else if (cell.getSheet().getRow(0).getCell(cell.getColumnIndex())
                    .getRichStringCellValue()
                    .toString()
                    .trim()
                    .equals("CPF") || cell.getSheet()
                    .getRow(0)
                    .getCell(cell.getColumnIndex())
                    .getRichStringCellValue()
                    .toString()
                    .trim()
                    .equals("TELEFONE")) {
                cell.setCellType(CellType.STRING);
            }
        }
        return linha;
    }

    public static boolean checkIfNotRowIsEmpty(Row row) {
        boolean linhaVazia = false;
        for (int cellNum = 0; cellNum < NumeroCelulaUtil.QNT_COL; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (!linhaVazia
                    && cell != null
                    && cell.getCellTypeEnum() != CellType.BLANK
                    && StringUtils.isNotBlank(cell.toString())) {
                linhaVazia = true;
            }
        }
        return linhaVazia;
    }
}
