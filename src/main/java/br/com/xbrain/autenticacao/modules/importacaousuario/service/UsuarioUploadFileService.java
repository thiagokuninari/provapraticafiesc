package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.adicionarZerosAEsquerda;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.isCpfValido;

@Service
public class UsuarioUploadFileService {

    private static final String SENHA_PADRAO = "102030";
    private static final int PRIMEIRA_POSICAO = 0;
    private static final int QNT_SENHA = 6;
    private static final int TAMANHO_MAX_EMAIL = 80;
    private static final int TAMANHO_MAX_NOME = 100;
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

    private final Logger log = LoggerFactory.getLogger(UsuarioUploadFileService.class);

    protected UsuarioImportacaoPlanilha processarUsuarios(Row row, UsuarioImportacaoRequest request) {
        String senhaDescriptografada = tratarSenha(request.isSenhaPadrao());
        UsuarioImportacaoPlanilha usuario = buildUsuario(row, senhaDescriptografada, request.isResetarSenhaUsuarioSalvo());
        validarUsuarioExistente(usuario, request.isResetarSenhaUsuarioSalvo());

        if (usuario.getMotivoNaoImportacao().isEmpty()) {
            Usuario usuarioSalvo = salvarUsuario(usuario);
            if (!request.isSenhaPadrao()) {
                notificarUsuario(usuarioSalvo, senhaDescriptografada);
            }
        }
        return usuario;
    }

    protected void notificarUsuario(Usuario usuarioSalvo, String senhaDescriptografada) {
        if (!ObjectUtils.isEmpty(usuarioSalvo) && !ObjectUtils.isEmpty(usuarioSalvo.getId())) {
            notificacaoService.enviarEmailDadosDeAcesso(usuarioSalvo, senhaDescriptografada);
        }
    }

    protected String tratarSenha(boolean senhaPadrao) {
        return senhaPadrao
                ? SENHA_PADRAO : getSenhaRandomica();
    }

    protected UsuarioImportacaoPlanilha buildUsuario(Row row, String senha, boolean resetarSenhaUsuarioSalvo) {

        Nivel nivel = recuperarNivel(row.getCell(NumeroCelulaUtil.CELULA_NIVEL).getStringCellValue());

        Departamento departamento = recuperarDepartamento(
                row.getCell(NumeroCelulaUtil.CELULA_DEPARTAMENTO)
                        .getStringCellValue(), nivel);

        Cargo cargo = recuperarCargo(row.getCell(NumeroCelulaUtil.CELULA_CARGO)
                .getStringCellValue(), nivel);

        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
                .builder()
                .nome(row.getCell(NumeroCelulaUtil.CELULA_NOME).getStringCellValue())
                .cpf(row.getCell(NumeroCelulaUtil.CELULA_CPF).getStringCellValue())
                .email(row.getCell(NumeroCelulaUtil.CELULA_EMAIL).getStringCellValue())
                .nascimento(trataData(row.getCell(NumeroCelulaUtil.CELULA_NACIMENTO)))
                .telefone(row.getCell(NumeroCelulaUtil.CELULA_TELEFONE).getStringCellValue())
                .senha(passwordEncoder.encode(senha))
                .departamento(departamento)
                .cargo(cargo)
                .nivel(nivel)
                .build();

        return validarUsuario(usuario, resetarSenhaUsuarioSalvo);
    }

    protected UsuarioImportacaoPlanilha validarUsuario(UsuarioImportacaoPlanilha usuario, boolean resetarSenhaUsuarioSalvo) {
        usuario.setMotivoNaoImportacao(
                Stream.of(
                        validarNivel(usuario),
                        validarEmail(usuario),
                        validarUsuarioExistente(usuario, resetarSenhaUsuarioSalvo),
                        validarCpf(usuario),
                        validarNome(usuario),
                        validarDepartamento(usuario),
                        validarCargo(usuario),
                        validarNascimento(usuario)
                ).filter(codigo -> !codigo.isEmpty())
                        .collect(Collectors.toList())
        );
        return usuario;
    }

    private String trataString(String valor) {
        return StringUtil.removerAcentos(valor)
                .trim()
                .replaceAll("[ -]", "_")
                .toUpperCase();
    }

    protected Departamento recuperarDepartamento(String departamentoStr, Nivel nivel) {
        Departamento departamento = null;
        if (nivel != null) {
            try {
                CodigoDepartamento codigoDepartamento = CodigoDepartamento
                        .valueOf(
                                trataString(departamentoStr)
                        );
                Optional<Departamento> optionalDepartamento = departamentoRepository
                        .findByCodigoAndNivelId(codigoDepartamento, nivel.getId());
                if (optionalDepartamento.isPresent()) {
                    departamento = optionalDepartamento.get();
                } else {
                    log.error("Não foi encontrado nenhum departamento com o nivelId "
                            + nivel.getId() + " e com o departamento " + departamentoStr);
                }
            } catch (IllegalArgumentException ex) {
                log.error("Erro ao recuperar departamento.", ex);
            }
        }
        return departamento;
    }

    protected Cargo recuperarCargo(String nome, Nivel nivel) {
        Cargo cargo = null;
        if (nivel != null) {
            Optional<Cargo> optionalCargo = cargoRepository
                    .findFirstByNomeIgnoreCaseContainingAndNivelId(nome, nivel.getId());
            if (optionalCargo.isPresent()) {
                cargo = optionalCargo.get();
            } else {
                log.error("Não foi encontrado nenhum cargo com o nivelId "
                        + nivel.getId() + " e com o nome " + nome);
            }
        }
        return cargo;
    }

    protected Nivel recuperarNivel(String codigoNivelStr) {
        Nivel nivelCanal = null;
        try {
            CodigoNivel codigoNivel = CodigoNivel.valueOf(trataString(codigoNivelStr));
            nivelCanal = nivelRepository.findByCodigo(codigoNivel);
        } catch (IllegalArgumentException ex) {
            log.error("Erro ao recuperar nivel.", ex);
        }
        return nivelCanal;
    }

    protected Usuario salvarUsuario(UsuarioImportacaoPlanilha usuario) {
        Usuario usuarioConvertido = UsuarioImportacaoPlanilha.convertFrom(usuario);
        return usuarioRepository.save(usuarioConvertido);
    }

    protected static String getSenhaRandomica() {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX_LONG);
        return tag.substring(PRIMEIRA_POSICAO, QNT_SENHA);
    }

    protected String validarUsuarioExistente(UsuarioImportacaoPlanilha usuario, boolean resetarSenhaUsuarioSalvo) {
        Optional<Usuario> usuariosSalvo = usuarioRepository.findByEmailIgnoreCaseOrCpf(usuario.getEmail(), usuario.getCpf());
        StringBuilder msgErro = new StringBuilder();
        if (usuariosSalvo.isPresent()) {
            msgErro.append("Usuário já salvo no banco");
            if (resetarSenhaUsuarioSalvo) {
                msgErro.append(tratarUsuarioSalvo(usuariosSalvo.get()));
            }
        }

        return msgErro.toString();
    }

    private String tratarUsuarioSalvo(Usuario usuario) {
        resetarSenhaUsuario(usuario);
        usuarioRepository.save(usuario);
        return ", sua senha foi resetada para a padrão.";
    }

    private void resetarSenhaUsuario(Usuario usuario) {
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSenha(passwordEncoder.encode(SENHA_PADRAO));
    }

    private String validarNivel(UsuarioImportacaoPlanilha usuario) {
        Nivel nivel = usuario.getNivel();
        return nivel == null
                ? "Falha ao recuperar cargo/nivel"
                : isNivelImportavel(nivel.getCodigo())
                ? ""
                : "O nível " + nivel.getCodigo() + " não é possivel importar via arquivo.";
    }

    private boolean isNivelImportavel(CodigoNivel nivel) {
        return !nivel.equals(CodigoNivel.MSO)
                && !nivel.equals(CodigoNivel.OPERACAO)
                && !nivel.equals(CodigoNivel.AGENTE_AUTORIZADO);
    }

    protected String validarEmail(UsuarioImportacaoPlanilha usuarioImportacaoPlanilha) {
        return !EmailUtil.validar(usuarioImportacaoPlanilha.getEmail())
                || usuarioImportacaoPlanilha.getEmail().length() > TAMANHO_MAX_EMAIL
                ? "O campo email está inválido." : "";
    }

    protected String validarCpf(UsuarioImportacaoPlanilha usuarioImportacaoPlanilha) {
        return !isCpfValido(adicionarZerosAEsquerda(usuarioImportacaoPlanilha.getCpf()))
                ? "O campo cpf está incorreto." : "";
    }

    protected String validarCargo(UsuarioImportacaoPlanilha usuario) {
        return usuario.getCargo() == null
                ? "Usuário está com cargo inválido" : "";
    }

    protected String validarDepartamento(UsuarioImportacaoPlanilha usuario) {
        return usuario.getDepartamento() == null
                ? "Usuário está com departamento inválido" : "";
    }

    protected String validarNome(UsuarioImportacaoPlanilha usuario) {
        return usuario.getNome() == null
                || usuario.getNome().isEmpty()
                || usuario.getNome().length() > TAMANHO_MAX_NOME
                ? "Usuário está com nome inválido" : "";
    }

    protected String validarNascimento(UsuarioImportacaoPlanilha usuario) {
        return usuario.getNascimento() == null
                || usuario.getNascimento().isAfter(LocalDateTime.now().minusHours(1L))
                ? "Usuário está com nascimento inválido" : "";
    }

    protected LocalDateTime trataData(Cell cellDate) {
        try {
            Date dataNascimento = cellDate.getDateCellValue();
            return dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ex) {
            log.error("Erro ao tratar data.", ex);
            return LocalDateTime.now();
        }
    }
}