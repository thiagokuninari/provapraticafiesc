package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.EmailUtil;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.adicionarZerosAEsquerda;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.isCpfValido;

@Service
public class UsuarioUploadFileService {

    private static final String SENHA_PADRAO = "102030";
    private static final int PRIMEIRA_POSICAO = 0;
    private static final int QNT_SENHA = 6;
    private static final int RADIX_LONG = 36;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioImportacaoPlanilha processarUsuarios(Row row, boolean senhaPadrao) {
        String senhaDescriptografada = tratarSenha(senhaPadrao);
        UsuarioImportacaoPlanilha usuario = buildUsuario(row, senhaDescriptografada);
        validarUsuarioExistente(usuario);

        if (usuario.getMotivoNaoImportacao().isEmpty()) {
            Usuario usuarioSalvo = salvarUsuario(usuario);
            notificarUsuario(usuarioSalvo, senhaDescriptografada, senhaPadrao);
        }
        return usuario;
    }

    public void notificarUsuario(Usuario usuarioSalvo, String senhaDescriptografada, boolean senhaPadrao) {
        if (!senhaPadrao && usuarioSalvo != null && usuarioSalvo.getId() != null) {
            notificacaoService.enviarEmailDadosDeAcesso(usuarioSalvo, senhaDescriptografada);
        }
    }

    public String tratarSenha(boolean senhaPadrao) {
        return senhaPadrao
                ? SENHA_PADRAO : getSenhaRandomica();
    }

    public UsuarioImportacaoPlanilha buildUsuario(Row row, String senha) {
        String codigoNivelStr = row.getCell(NumeroCelulaUtil.CELULA_ZERO)
                .getStringCellValue().replaceAll(" ", "_");
        Nivel nivel = recuperarNivel(codigoNivelStr);
        List<String> motivos = new ArrayList<>();
        Cargo cargo = null;
        Departamento departamento = null;
        if (nivel != null) {
            Optional<Cargo> optionalCargo = recuperarCargo(row.getCell(NumeroCelulaUtil.CELULA_UM)
                    .getStringCellValue(), nivel.getId());
            if (optionalCargo.isPresent()) {
                cargo = optionalCargo.get();
            }
            Optional<Departamento> optionalDepartamento = recuperarDepartamento(nivel.getId());
            if (optionalDepartamento.isPresent()) {
                departamento = optionalDepartamento.get();
            }
        } else {
            motivos.add("Falha ao recuperar cargo/nivel");
        }
        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
                .builder()
                .nome(row.getCell(NumeroCelulaUtil.CELULA_DOIS).getStringCellValue())
                .cpf(row.getCell(NumeroCelulaUtil.CELULA_TRES).getStringCellValue())
                .email(row.getCell(NumeroCelulaUtil.CELULA_QUATRO).getStringCellValue())
                .nascimento(trataData(row.getCell(NumeroCelulaUtil.CELULA_CINCO).getDateCellValue()))
                .telefone(row.getCell(NumeroCelulaUtil.CELULA_SEIS).getStringCellValue())
                .senha(passwordEncoder.encode(senha))
                .motivoNaoImportacao(motivos)
                .departamento(departamento)
                .cargo(cargo)
                .build();

        return validarUsuario(usuario);
    }

    private UsuarioImportacaoPlanilha validarUsuario(UsuarioImportacaoPlanilha usuario) {
        List<String> motivos = new ArrayList<>();
        motivos.add(validarEmail(usuario));
        motivos.add(validarUsuarioExistente(usuario));
        motivos.add(validarCpf(usuario));
        motivos.add(validarNome(usuario));
        motivos.add(validarDepartamento(usuario));
        motivos.add(validarCargo(usuario));
        motivos.add(validarNascimento(usuario));

        motivos = motivos.stream()
                .filter(motivo -> !motivo.isEmpty())
                .collect(Collectors.toList());

        usuario.getMotivoNaoImportacao().addAll(motivos);
        return usuario;
    }

    private Optional<Departamento> recuperarDepartamento(Integer id) {
        return departamentoRepository.findByCodigoAndNivelId(CodigoDepartamento.COMERCIAL, id);

    }

    private Optional<Cargo> recuperarCargo(String nome, Integer id) {
        return cargoRepository.findByNomeIgnoreCaseContainingAndNivelId(nome, id);
    }

    private Nivel recuperarNivel(String codigoNivelStr) {
        Nivel nivelCanal = null;

        try {
            CodigoNivel codigoNivel = CodigoNivel.valueOf(codigoNivelStr);
            nivelCanal = nivelRepository.findByCodigo(codigoNivel);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        return nivelCanal;
    }

    private Usuario salvarUsuario(UsuarioImportacaoPlanilha usuario) {
        Usuario usuarioConvertido = UsuarioImportacaoPlanilha.convertFrom(usuario);
        return usuarioRepository.save(usuarioConvertido);
    }

    private static String getSenhaRandomica() {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX_LONG);
        return tag.substring(PRIMEIRA_POSICAO, QNT_SENHA);
    }

    public String validarUsuarioExistente(UsuarioImportacaoPlanilha usuario) {
        Integer qntUsuariosSalvos = usuarioRepository.countByEmailOrCpf(usuario.getEmail(), usuario.getCpf());

        return (qntUsuariosSalvos != 0)
                ? "Usuário já salvo no banco" : "";
    }

    public String validarEmail(UsuarioImportacaoPlanilha usuarioImportacaoPlanilha) {
        return !EmailUtil.validar(usuarioImportacaoPlanilha.getEmail())
                ? "O campo email está incorreto." : "";
    }

    public String validarCpf(UsuarioImportacaoPlanilha usuarioImportacaoPlanilha) {
        return !isCpfValido(adicionarZerosAEsquerda(usuarioImportacaoPlanilha.getCpf()))
                ? "O campo cpf está incorreto." : "";
    }

    public String validarCargo(UsuarioImportacaoPlanilha usuario) {
        return usuario.getCargo() == null
                ? "Usuário está com cargo inválido" : "";
    }

    public String validarDepartamento(UsuarioImportacaoPlanilha usuario) {
        return usuario.getDepartamento() == null
                ? "Usuário está com departamento inválido" : "";
    }

    public String validarNome(UsuarioImportacaoPlanilha usuario) {
        return usuario.getNome() == null || usuario.getNome().isEmpty()
                ? "Usuário está com nome inválido" : "";
    }

    public String validarNascimento(UsuarioImportacaoPlanilha usuario) {
        return usuario.getNascimento() == null
                || usuario.getNascimento().isAfter(LocalDateTime.now().minusHours(1L))
                ? "Usuário está com nascimento inválido" : "";
    }

    private static LocalDateTime trataData(Date dataNascimento) {
        try {
            return dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }
}