package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.util.EmailUtil;
import br.com.xbrain.autenticacao.modules.usuario.util.NumeroCelulaUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

import static br.com.xbrain.autenticacao.modules.usuario.util.CpfUtil.adicionarZerosAEsquerda;
import static br.com.xbrain.autenticacao.modules.usuario.util.CpfUtil.isCpfValido;

@Service
public class UsuarioUploadFileService {

    private static final String SENHA_PADRAO = "102030";
    private static final int PRIMEIRA_POSICAO = 0;
    private static final int QNT_SENHA = 6;
    private static final int RADIX_LONG = 36;

    @Autowired
    CargoRepository cargoRepository;
    @Autowired
    NivelRepository nivelRepository;
    @Autowired
    DepartamentoRepository departamentoRepository;

    public UsuarioImportacaoRequest build(Row row, boolean senhaPadrao) {
        Nivel nivelCanal;
        UsuarioImportacaoRequest usuario = new UsuarioImportacaoRequest();
        try {
            CodigoNivel codigoNivel = CodigoNivel.valueOf(
                    validaCampo(row.getCell(NumeroCelulaUtil.CELULA_ZERO), usuario).replaceAll(" ", "_"));

            nivelCanal = nivelRepository.findByCodigo(codigoNivel);

            if (nivelCanal != null) {
                cargoRepository.findByNomeIgnoreCaseContainingAndNivelId(
                        validaCampo(row.getCell(NumeroCelulaUtil.CELULA_UM), usuario), nivelCanal.getId())
                        .ifPresent(usuario::setCargo);

                departamentoRepository.findByCodigoAndNivelId(CodigoDepartamento.COMERCIAL, nivelCanal.getId())
                        .ifPresent(usuario::setDepartamento);
            } else {
                usuario.getMotivoNaoImportacao().add("Falha ao recuperar cargo/nivel");
            }
            usuario.setNome(validaCampo(row.getCell(NumeroCelulaUtil.CELULA_DOIS), usuario));
            usuario.setCpf(validarCpf(row.getCell(NumeroCelulaUtil.CELULA_TRES).getStringCellValue(), usuario));
            usuario.setEmail(validarEmail(row.getCell(NumeroCelulaUtil.CELULA_QUATRO).getStringCellValue(), usuario));
            usuario.setNascimento(trataData(row.getCell(NumeroCelulaUtil.CELULA_CINCO).getDateCellValue(), usuario));
            usuario.setTelefone(validaCampo(row.getCell(NumeroCelulaUtil.CELULA_SEIS), usuario));

            usuario.setSenha(senhaPadrao ? SENHA_PADRAO : getSenhaRandomica(QNT_SENHA));
            usuario.setAlterarSenha(Eboolean.V);
            usuario.setDataCadastro(LocalDateTime.now());

            usuario.setSituacao(ESituacao.A);

            return usuario;

        } catch (Exception ex) {
            ex.printStackTrace();
            return usuario;
        }
    }

    private static String getSenhaRandomica(int size) {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX_LONG);
        return tag.substring(PRIMEIRA_POSICAO, size);
    }

    private String validaCampo(Cell cell, UsuarioImportacaoRequest usuarioImportacaoRequest) {

        String valor = cell.getStringCellValue();
        if (valor != null && !valor.isEmpty()) {
            return valor;
        }
        usuarioImportacaoRequest.getMotivoNaoImportacao()
                .add("O campo " + cell.getSheet().getRow(NumeroCelulaUtil.CELULA_ZERO).getCell(cell.getColumnIndex())
                .getRichStringCellValue()
                        .toString()
                        .toLowerCase() + " esta incorreto.");
        return "";

    }

    private String validarEmail(String email, UsuarioImportacaoRequest usuarioImportacaoRequest) {

        if (EmailUtil.validar(email)) {
            return email;
        }
        usuarioImportacaoRequest.getMotivoNaoImportacao().add("O campo email esta incorreto.");
        return "";
    }

    private static String validarCpf(String cpf, UsuarioImportacaoRequest usuarioImportacaoRequest) {
        if (isCpfValido(adicionarZerosAEsquerda(cpf))) {
            return cpf;
        }
        usuarioImportacaoRequest.getMotivoNaoImportacao().add("O campo cpf esta incorreto.");
        return "";
    }

    private static LocalDateTime trataData(Date dataNascimento, UsuarioImportacaoRequest usuarioImportacaoRequest) {

        try {
            return dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ex) {
            usuarioImportacaoRequest.getMotivoNaoImportacao().add("O campo data de nascimento esta invalida.");
            return LocalDateTime.now();

        }

    }

}
