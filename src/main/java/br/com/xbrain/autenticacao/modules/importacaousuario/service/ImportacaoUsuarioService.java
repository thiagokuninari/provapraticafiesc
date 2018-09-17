package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil;
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

    public List<UsuarioImportacaoPlanilha> readFile(MultipartFile file, UsuarioImportacaoRequest usuario) {
        try {
            Sheet sheet = planilhaService.getSheet(file);
            if (sheet.getRow(NumeroCelulaUtil.CELULA_ZERO).getLastCellNum() < NumeroCelulaUtil.QNT_COL
                    || !validarColunas(sheet.getRow(NumeroCelulaUtil.CELULA_ZERO))) {
                throw new ValidacaoException("Erro. Arquivo Inválido.");
            }
            return StreamSupport
                    .stream(sheet.spliterator(), false)
                    .filter(row -> row.getRowNum() > NumeroCelulaUtil.CELULA_ZERO)
                    .filter(PlanilhaService::checkIfNotRowIsEmpty)
                    .map(PlanilhaService::converterTipoCelulaParaString)
                    .map(row -> usuarioUploadFile.processarUsuarios(row, usuario.isSenhaPadrao()))
                    .collect(Collectors.toList());
        } catch (ValidacaoException ex) {
            ex.printStackTrace();
            throw new ValidacaoException("Erro. Arquivo Inválido.");

        }
    }

    private boolean validarColunas(Row row) {
        return PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_ZERO), "NIVEL/CANAL")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_UM), "CARGO")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_DOIS), "NOME")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_TRES), "CPF")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_QUATRO), "E-MAIL")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_CINCO), "DATANASCIMENTO")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_SEIS), "TELEFONE");
    }

    public List<UsuarioImportacaoResponse> salvarUsuarioFile(MultipartFile file, UsuarioImportacaoRequest usuario) {
        List<UsuarioImportacaoPlanilha> usuarioUploadFiles = readFile(
                file, usuario);

        return UsuarioImportacaoResponse.convertFrom(usuarioUploadFiles);
    }
}
