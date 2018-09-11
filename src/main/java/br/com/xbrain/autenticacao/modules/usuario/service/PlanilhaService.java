package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

@Component
public class PlanilhaService {
    String tipoArquivo = ".xlsx";

    public Sheet getSheet(MultipartFile file) throws ValidacaoException {
        try {
            Sheet sheet = null;
            String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));

            if (extensao.equals(tipoArquivo)) {
                sheet = new XSSFWorkbook(file.getInputStream()).getSheetAt(0);
            } else {
                throw new ValidacaoException("Não foi possivel reconhecer o formato do arquivo");
            }
            return sheet;
        } catch (ValidacaoException | IOException ex) {
            throw new ValidacaoException("Não foi possivel recuperar o sheet");
        }
    }

    public static Row converterTipoCelulaParaString(Row linha) {
        linha.forEach(cell -> {
            if (cell == null) {
                linha.createCell(cell.getColumnIndex(), CELL_TYPE_STRING);
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
                cell.setCellType(CELL_TYPE_STRING);
            }
        });
        return linha;
    }
}
