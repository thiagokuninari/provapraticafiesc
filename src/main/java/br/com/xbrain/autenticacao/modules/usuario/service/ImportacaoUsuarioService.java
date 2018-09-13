package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
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
    @Autowired
    private UsuarioRepository usuarioRepository;

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
                    .map(row -> usuarioUploadFile.build(row, isSenhaPadrao))
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
        return row.getCell(
                NumeroCelulaUtil.CELULA_ZERO)
                .getRichStringCellValue()
                .toString()
                .trim()
                .toUpperCase()
                .equals("NIVEL/CANAL")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_UM)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("CARGO")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_DOIS)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("NOME")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_TRES)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("CPF")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_QUATRO)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("E-MAIL")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_CINCO)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("DATANASCIMENTO")
                &&
                row.getCell(
                        NumeroCelulaUtil.CELULA_SEIS)
                        .getRichStringCellValue()
                        .toString()
                        .trim()
                        .toUpperCase()
                        .equals("TELEFONE");
    }

    public boolean validarUsuarioExistente(Usuario usuario) {
        return usuarioRepository.countByEmailOrCpf(usuario.getEmail(), usuario.getCpf()) == 0;
    }

    public List<Usuario> removerUsuariosJaSalvos(List<Usuario> usuarios) {
        return usuarios.stream()
                .filter(this::validarUsuarioExistente)
                .collect(Collectors.toList());
    }

    public List<UsuarioImportacaoResponse> salvarUsuarioFile(MultipartFile file,
                                                             UsuarioImportacaoRequest usuarioImportacaoRequest) {

        List<UsuarioImportacaoRequest> usuarioUploadFiles = readFile(
                file, usuarioImportacaoRequest.isSenhaPadrao());

        List<Usuario> usuariosToSave = filtrarUsuariosParaSalvar(usuarioUploadFiles);

        List<Usuario> usuarios = removerUsuariosJaSalvos(usuariosToSave);

        usuarioRepository.save(usuarios);

        return UsuarioImportacaoResponse.convertFrom(usuarioUploadFiles);
    }

    private List<Usuario> filtrarUsuariosParaSalvar(List<UsuarioImportacaoRequest> usuarioUploadFiles) {
        return usuarioUploadFiles.stream()
                .filter(user -> user.getMotivoNaoImportacao().isEmpty())
                .map(UsuarioImportacaoRequest::convertTo)
                .collect(Collectors.toList());
    }

}
