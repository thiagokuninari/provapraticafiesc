package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(ImportacaoUsuarioService.class);

    public List<UsuarioImportacaoPlanilha> readFile(MultipartFile file, UsuarioImportacaoRequest usuario) {
        try {
            Sheet sheet = planilhaService.getSheet(file);
            if (sheet.getRow(NumeroCelulaUtil.PRIMEIRA_LINHA).getLastCellNum() < NumeroCelulaUtil.QNT_COL
                    || !validarColunas(sheet.getRow(NumeroCelulaUtil.PRIMEIRA_LINHA))) {
                throw new ValidacaoException("Erro. Arquivo Inválido.");
            }
            return StreamSupport
                    .stream(sheet.spliterator(), true)
                    .filter(row -> row.getRowNum() > NumeroCelulaUtil.PRIMEIRA_LINHA)
                    .filter(PlanilhaService::checkIfNotRowIsEmpty)
                    .map(PlanilhaService::converterTipoCelulaParaString)
                    .map(row -> usuarioUploadFile.processarUsuarios(row, usuario))
                    .collect(Collectors.toList());
        } catch (ValidacaoException ex) {
            log.error("Erro. Arquivo Inválido.", ex);
            throw new ValidacaoException("Erro. Arquivo Inválido.");

        }
    }

    private boolean validarColunas(Row row) {
        return PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_NIVEL), "NIVEL/CANAL")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_CARGO), "CARGO")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_NOME), "NOME")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_DEPARTAMENTO), "DEPARTAMENTO")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_CPF), "CPF")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_EMAIL), "E-MAIL")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_NACIMENTO), "DATA NASCIMENTO")
                && PlanilhaService.compararColunas(row.getCell(NumeroCelulaUtil.CELULA_TELEFONE), "TELEFONE");
    }

    public List<UsuarioImportacaoResponse> salvarUsuarioFile(MultipartFile file, UsuarioImportacaoRequest usuario) {
        List<UsuarioImportacaoPlanilha> usuarioUploadFiles = readFile(
                file, usuario);

        return UsuarioImportacaoResponse.convertFrom(usuarioUploadFiles);
    }
}
