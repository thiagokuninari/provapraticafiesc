package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.util.NumeroCelulaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ImportacaoUsuarioService {

    @Autowired
    private PlanilhaService planilhaService;
    @Autowired
    private UsuarioUploadFileService usuarioUploadFile;

    public List<UsuarioImportacaoRequest> readFile(MultipartFile file, boolean isSenhaPadrao) {
        try {
            Sheet sheet = planilhaService.getSheet(file);
            if (sheet.getRow(NumeroCelulaUtil.CELULA_ZERO).getLastCellNum() < NumeroCelulaUtil.QNT_COL
                    && !validarColunas(sheet.getRow(NumeroCelulaUtil.CELULA_ZERO))) {
                throw new ValidacaoException("Erro. Arquivo Inválido.");
            }
            return StreamSupport
                    .stream(sheet.spliterator(), false)
                    .filter(row -> row.getRowNum() > NumeroCelulaUtil.CELULA_ZERO)
                    .filter(this::checkIfNotRowIsEmpty)
                    .map(PlanilhaService::converterTipoCelulaParaString)
                    .map(row -> usuarioUploadFile.processarUsuarios(row, isSenhaPadrao))
                    .collect(Collectors.toList());
        } catch (ValidacaoException ex) {
            ex.printStackTrace();
            throw new ValidacaoException("Erro. Arquivo Inválido.");

        }
    }

    private boolean checkIfNotRowIsEmpty(Row row) {
        if (row == null) {
            return false;
        }
        if (row.getLastCellNum() <= 0) {
            return false;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean validarColunas(Row row) {
        return compararColunas(row.getCell(NumeroCelulaUtil.CELULA_ZERO), "NIVEL/CANAL")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_UM), "CARGO")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_DOIS), "NOME")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_TRES), "CPF")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_QUATRO), "E-MAIL")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_CINCO), "DATANASCIMENTO")
                && compararColunas(row.getCell(NumeroCelulaUtil.CELULA_SEIS), "TELEFONE");
    }

    private boolean compararColunas(Cell cell, String valorColuna) {
        return cell.getRichStringCellValue()
                .toString()
                .trim()
                .toUpperCase().equals(valorColuna);
    }

    public List<UsuarioImportacaoResponse> salvarUsuarioFile(MultipartFile file,UsuarioImportacaoRequest usuario) {
        List<UsuarioImportacaoRequest> usuarioUploadFiles = readFile(
                file, usuario.isSenhaPadrao());

        return UsuarioImportacaoResponse.convertFrom(usuarioUploadFiles);
    }
}
