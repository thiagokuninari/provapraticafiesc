package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.EmailUtil;
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
import br.com.xbrain.xbrainutils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.adicionarZerosAEsquerda;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.CpfUtil.isCpfValido;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil.*;

@Service
@Slf4j
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

    protected static String getSenhaRandomica() {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX_LONG);
        return tag.substring(PRIMEIRA_POSICAO, QNT_SENHA);
    }

    protected UsuarioImportacaoPlanilha processarUsuarios(Row row, UsuarioImportacaoRequest request) {
        String senhaDescriptografada = tratarSenha(request.isSenhaPadrao());
        UsuarioImportacaoPlanilha usuario = buildUsuario(row, senhaDescriptografada, request.isResetarSenhaUsuarioSalvo());

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

    protected String tratarSenha(boolean isSenhaPadrao) {
        return isSenhaPadrao ? SENHA_PADRAO : getSenhaRandomica();
    }

    protected UsuarioImportacaoPlanilha buildUsuario(Row row, String senha, boolean resetarSenhaUsuarioSalvo) {
        Nivel nivel = recuperarNivel(row.getCell(CELULA_NIVEL).getStringCellValue());

        UsuarioImportacaoPlanilha usuario = UsuarioImportacaoPlanilha
            .builder()
            .nome(recuperarValorCelula(row, CELULA_NOME))
            .cpf(NumberUtils.getOnlyNumbers(recuperarValorCelula(row, CELULA_CPF)))
            .email(recuperarValorCelula(row, CELULA_EMAIL))
            .nascimento(trataData(row.getCell(CELULA_NACIMENTO)))
            .telefone(recuperarValorCelula(row, CELULA_TELEFONE))
            .senha(passwordEncoder.encode(senha))
            .departamento(recuperarDepartamento(row.getCell(CELULA_DEPARTAMENTO).getStringCellValue(), nivel))
            .cargo(recuperarCargo(row.getCell(CELULA_CARGO).getStringCellValue(), nivel))
            .nivel(nivel)
            .build();

        return validarUsuario(usuario, resetarSenhaUsuarioSalvo);
    }

    private String recuperarValorCelula(Row row, int celulaNome) {
        return row.getCell(celulaNome).getStringCellValue().trim();
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
        return departamentoRepository
            .findByCodigoAndNivelId(CodigoDepartamento.valueOf(trataString(departamentoStr)), nivel.getId())
            .orElse(null);
    }

    protected Cargo recuperarCargo(String nome, Nivel nivel) {
        return cargoRepository
            .findFirstByNomeIgnoreCaseAndNivelId(nome.trim(), nivel.getId())
            .orElse(null);
    }

    protected Nivel recuperarNivel(String codigoNivelStr) {
        try {
            return nivelRepository.findByCodigo(CodigoNivel.valueOf(trataString(codigoNivelStr)));
        } catch (IllegalArgumentException ex) {
            log.error("Erro ao recuperar nivel.", ex);
        }
        return null;
    }

    protected Usuario salvarUsuario(UsuarioImportacaoPlanilha usuario) {
        return usuarioRepository.save(UsuarioImportacaoPlanilha.of(usuario));
    }

    protected String validarUsuarioExistente(UsuarioImportacaoPlanilha usuario, boolean resetarSenhaUsuarioSalvo) {
        StringBuilder msgErro = new StringBuilder();

        List<Usuario> listaUsuariosPresentes = usuarioRepository
                .findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(usuario.getEmail(), usuario.getCpf(), ESituacao.R);

        if (listaUsuariosPresentes.size() > 0) {
            msgErro.append("Usuário já salvo no banco");
            if (resetarSenhaUsuarioSalvo) {
                listaUsuariosPresentes.parallelStream().forEach(this::tratarUsuarioSalvo);
                msgErro.append(", sua senha foi resetada para a padrão.");
            }
        }
        return msgErro.toString();
    }

    private void tratarUsuarioSalvo(Usuario usuario) {
        resetarSenhaUsuario(usuario);
        usuario.removerCaracteresDoCpf();
        usuarioRepository.save(usuario);
    }

    private void resetarSenhaUsuario(Usuario usuario) {
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSenha(passwordEncoder.encode(SENHA_PADRAO));
    }

    private String validarNivel(UsuarioImportacaoPlanilha usuario) {
        Nivel nivel = usuario.getNivel();
        return ObjectUtils.isEmpty(nivel)
                ? "Falha ao recuperar cargo/nível"
                : isNivelImportavel(nivel.getCodigo())
                ? ""
                : "O nível " + nivel.getCodigo() + " não é possível importar via arquivo.";
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
        return ObjectUtils.isEmpty(usuario.getCargo())
                ? "Usuário está com cargo inválido" : "";
    }

    protected String validarDepartamento(UsuarioImportacaoPlanilha usuario) {
        return ObjectUtils.isEmpty(usuario.getDepartamento())
                ? "Usuário está com departamento inválido" : "";
    }

    protected String validarNome(UsuarioImportacaoPlanilha usuario) {
        return ObjectUtils.isEmpty(usuario.getNome())
                || usuario.getNome().isEmpty()
                || usuario.getNome().length() > TAMANHO_MAX_NOME
                ? "Usuário está com nome inválido" : "";
    }

    protected String validarNascimento(UsuarioImportacaoPlanilha usuario) {
        return ObjectUtils.isEmpty(usuario.getNascimento())
                || usuario.getNascimento().isAfter(LocalDateTime.now().minusHours(1L))
                ? "Usuário está com nascimento inválido" : "";
    }

    protected LocalDateTime trataData(Cell cellDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return cellDate.getCellTypeEnum().equals(CellType.STRING)
                    ? LocalDate.parse(cellDate.getStringCellValue(), formatter).atStartOfDay()
                    : cellDate.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ex) {
            log.error("Erro ao tratar data.", ex);
            return LocalDateTime.now();
        }
    }
}
