package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@Slf4j
public class UsuarioAcessoService {

    private static final int TRINTA_E_DOIS_DIAS = 32;
    private static final String MSG_ERRO_AO_INATIVAR_USUARIO = "ocorreu um erro desconhecido ao inativar "
        + "usuários que estão a mais de 32 dias sem efetuar login.";
    private static final String MSG_ERRO_AO_DELETAR_REGISTROS = "Ocorreu um erro desconhecido ao tentar deletar "
        + " os registros antigos da tabela usuário acesso.";
    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;

    @Transactional
    public void registrarAcesso(Integer usuarioId) {
        usuarioAcessoRepository.save(UsuarioAcesso.builder()
            .build()
            .criaRegistroAcesso(usuarioId));
    }

    @Async
    @Transactional
    public void registrarLogout(Integer usuarioId) {
        usuarioAcessoRepository.save(UsuarioAcesso.criaRegistroLogout(usuarioId));
    }

    @Transactional
    public void inativarUsuariosSemAcesso() {
        usuarioIsXbrain();
        try {
            List<UsuarioAcesso> usuarios = buscarUsuariosParaInativar();
            usuarios.forEach(usuarioAcesso -> {
                Usuario usuario = usuarioAcesso.getUsuario();
                usuarioRepository.atualizarParaSituacaoInativo(usuario.getId());
                usuarioHistoricoService.gerarHistoricoInativacao(usuario);
                inativarColaboradorPol(usuario);
            });
            log.info("Total de usuários inativados: " + usuarios.size());
        } catch (Exception ex) {
            log.warn(MSG_ERRO_AO_INATIVAR_USUARIO, ex);
        }
    }

    @Transactional
    public long deletarHistoricoUsuarioAcesso() {
        usuarioIsXbrain();
        try {
            return usuarioAcessoRepository.deletarHistoricoUsuarioAcesso();
        } catch (Exception ex) {
            log.warn(MSG_ERRO_AO_DELETAR_REGISTROS, ex);
        }
        return 0;
    }

    private boolean ultrapassouTrintaEDoisDiasDesdeUltimoAcesso(UsuarioAcesso usuarioAcesso) {
        return usuarioAcesso.getDataCadastro()
            .isBefore(LocalDateTime.now().minusDays(TRINTA_E_DOIS_DIAS));
    }

    private List<UsuarioAcesso> buscarUsuariosParaInativar() {
        List<UsuarioAcesso> usuariosAcesso = usuarioAcessoRepository.findAllUltimoAcessoUsuarios()
            .stream()
            .filter(this::ultrapassouTrintaEDoisDiasDesdeUltimoAcesso)
            .collect(Collectors.toList());

        List<UsuarioAcesso> usuarios = usuarioRepository.findAllUsuariosSemDataUltimoAcesso()
            .stream()
            .map(UsuarioAcesso::of)
            .collect(Collectors.toList());
        return retornarListaUsuariosParaInativar(usuariosAcesso, usuarios);
    }

    private List<UsuarioAcesso> retornarListaUsuariosParaInativar(List<UsuarioAcesso> usuariosAcesso,
                                                                  List<UsuarioAcesso> usuarios) {
        if (!usuariosAcesso.isEmpty() && !usuarios.isEmpty()) {
            usuariosAcesso.addAll(usuarios);
            return usuariosAcesso;
        } else if (!usuariosAcesso.isEmpty()) {
            return usuariosAcesso;
        }
        return usuarios;
    }

    private void inativarColaboradorPol(Usuario usuario) {
        if (Objects.nonNull(usuario.getEmail())) {
            inativarColaboradorMqSender.sendSuccess(usuario.getEmail());
        } else {
            log.warn("Usuário " + usuario.getId() + " não possui um email cadastrado.");
        }
    }

    private void usuarioIsXbrain() {
        if (!autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            throw new PermissaoException();
        }
    }

    public Page<UsuarioAcessoResponse> getAll(PageRequest pageRequest, UsuarioAcessoFiltros usuarioAcessoFiltros) {
        if (!ObjectUtils.isEmpty(usuarioAcessoFiltros.getAaId())) {
            usuarioAcessoFiltros.setAgenteAutorizadosIds(getIdUsuariosByAaId(usuarioAcessoFiltros));
        }

        var lista = StreamSupport
            .stream(usuarioAcessoRepository
                .findAll(usuarioAcessoFiltros.toPredicate(), pageRequest).spliterator(), false)
            .map(UsuarioAcessoResponse::of)
            .distinct()
            .collect(Collectors.toList());
        return new PageImpl<>(lista, pageRequest, getCountDistinct(usuarioAcessoFiltros));
    }

    private long getCountDistinct(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        return StreamSupport
            .stream(usuarioAcessoRepository
                .findAll(usuarioAcessoFiltros.toPredicate()).spliterator(), false)
            .map(UsuarioAcessoResponse::of)
            .distinct()
            .count();
    }

    private List<Integer> getIdUsuariosByAaId(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        return agenteAutorizadoClient.getUsuariosByAaId(usuarioAcessoFiltros.getAaId(), false)
            .stream()
            .map(UsuarioAgenteAutorizadoResponse::getId)
            .collect(Collectors.toList());
    }

    public void exportRegistrosToCsv(HttpServletResponse response, UsuarioAcessoFiltros usuarioAcessoFiltros) {
        var registros = getRegistros(usuarioAcessoFiltros);

        if (!CsvUtils.setCsvNoHttpResponse(
            getCsv(registros),
            "REGISTROS " + LocalDateTime.now(),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório.");
        }
    }

    public List<UsuarioAcessoResponse> getRegistros(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        if (!ObjectUtils.isEmpty(usuarioAcessoFiltros.getAaId())) {
            usuarioAcessoFiltros.setAgenteAutorizadosIds(getIdUsuariosByAaId(usuarioAcessoFiltros));
        }
        return StreamSupport
            .stream(usuarioAcessoRepository
                .findAll(usuarioAcessoFiltros.toPredicate()).spliterator(), false)
            .map(UsuarioAcessoResponse::of)
            .distinct()
            .sorted(Comparator.comparing(UsuarioAcessoResponse::getDataHora).reversed())
            .collect(Collectors.toList());
    }

    public String getCsv(List<UsuarioAcessoResponse> lista) {
        return UsuarioAcessoResponse.getCabecalhoCsv()
            + (!lista.isEmpty()
            ? lista.stream()
            .map(UsuarioAcessoResponse::toCsv)
            .collect(Collectors.joining("\n"))
            : "Registros não encontrados.");
    }
}
