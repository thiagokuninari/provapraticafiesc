package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.util.NumeroCelulaUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
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
                    && validarColunas(sheet.getRow(NumeroCelulaUtil.CELULA_ZERO))) {
                throw new ValidacaoException("Erro. Arquivo Inválido.");
            }
            return StreamSupport
                    .stream(sheet.spliterator(), false)
                    .filter(row -> row.getRowNum() > NumeroCelulaUtil.CELULA_ZERO)
                    .filter(row -> !ObjectUtils.isEmpty(row.getCell(NumeroCelulaUtil.CELULA_SEIS)))
                    .map(PlanilhaService::converterTipoCelulaParaString)
                    .map(row -> usuarioUploadFile.build(row, isSenhaPadrao))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (ValidacaoException ex) {
            ex.printStackTrace();
            throw new ValidacaoException("Erro. Arquivo Inválido.");

        }
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

    public void validarUsuarioFile(List<Usuario> usuarioUploadFiles) {
        if (usuarioUploadFiles.isEmpty()) {
            throw new ValidacaoException("Erro. Nenhum usuario encontrado no arquivo!");
        }
    }

    public List<Usuario> removerUsuariosJaSalvos(List<Usuario> usuarios) {
        return usuarios.stream()
                .filter(this::validarUsuarioExistente)
                .collect(Collectors.toList());
    }

    public List<UsuarioImportacaoResponse> salvarUsuarioFile(MultipartFile file, boolean isSenhaPadrao) {
        List<UsuarioImportacaoRequest> usuarioUploadFiles = readFile(file, isSenhaPadrao);

        List<Usuario> usuariosToSave = filtrarUsuariosParaSalvar(usuarioUploadFiles);

        List<Usuario> usuarios = removerUsuariosJaSalvos(usuariosToSave);

        validarUsuarioFile(usuarios);

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
