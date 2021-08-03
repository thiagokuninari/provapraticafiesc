package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.util.ObjectUtils.isEmpty;

@Transactional
@Service
@Slf4j
public class UsuarioAcessoService {

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
    private AgenteAutorizadoNovoClient agenteAutorizadoNovoClient;
    @Autowired
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Value("${app-config.timer-usuario.data-hora-inativar-usuario-a-partir-de}")
    private String dataHoraInativarUsuario;

    @Value("${app-config.timer-usuario.email-usuario-viabilidade}")
    private String emailUsuarioViabilidade;

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
    public Integer inativarUsuariosSemAcesso(String origem) {
        int usuariosInativados = 0;
        try {
            List<UsuarioAcesso> usuarios = buscarUsuariosParaInativar(LocalDateTime.parse(dataHoraInativarUsuario));
            usuarios.forEach(usuarioAcesso -> {
                Usuario usuario = usuarioAcesso.getUsuario();
                usuarioRepository.atualizarParaSituacaoInativo(usuario.getId());
                usuarioHistoricoService.gerarHistoricoInativacao(usuario, origem);
                inativarColaboradorPol(usuario);
            });
            usuariosInativados = usuarios.size();
        } catch (Exception ex) {
            log.warn(MSG_ERRO_AO_INATIVAR_USUARIO, ex);
        }
        return usuariosInativados;
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

    private List<UsuarioAcesso> buscarUsuariosParaInativar(LocalDateTime dataHoraInativarUsuario) {
        List<UsuarioAcesso> usuariosUnificados =
            unificar(getUsuariosUltimoAcessoExpirado(dataHoraInativarUsuario),
                getUsuariosSemUltimoAcesso(dataHoraInativarUsuario));
        removerUsuarioViabilidade(usuariosUnificados);
        return usuariosUnificados;
    }

    private void removerUsuarioViabilidade(List<UsuarioAcesso> usuariosUnificados) {
        usuariosUnificados.removeIf(u ->
            emailUsuarioViabilidade.equals(u.getUsuario().getEmail() != null ? u.getUsuario().getEmail() : ""));
    }

    private List<UsuarioAcesso> getUsuariosSemUltimoAcesso(LocalDateTime dataHoraInativarUsuario) {
        return usuarioRepository.findAllUsuariosSemDataUltimoAcesso(dataHoraInativarUsuario)
            .stream()
            .map(UsuarioAcesso::of)
            .collect(Collectors.toList());
    }

    private List<UsuarioAcesso> getUsuariosUltimoAcessoExpirado(LocalDateTime dataHoraInativarUsuario) {
        return usuarioAcessoRepository.findAllUltimoAcessoUsuarios(dataHoraInativarUsuario);
    }

    private List<UsuarioAcesso> unificar(List<UsuarioAcesso> usuariosAcesso, List<UsuarioAcesso> usuarios) {
        List<UsuarioAcesso> usuariosUnificados = new ArrayList<>();
        if (!usuariosAcesso.isEmpty()) {
            usuariosUnificados.addAll(usuariosAcesso);
        }
        if (!usuarios.isEmpty()) {
            usuariosUnificados.addAll(usuarios);
        }
        return usuariosUnificados;
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
        if (!isEmpty(usuarioAcessoFiltros.getAaId())) {
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
        return agenteAutorizadoNovoClient.getUsuariosByAaId(usuarioAcessoFiltros.getAaId(), false)
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
        if (!isEmpty(usuarioAcessoFiltros.getAaId())) {
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

    public List<PaLogadoDto> getTotalUsuariosLogadosPorPeriodoByFiltros(UsuarioLogadoRequest usuarioLogadoRequest) {
        var usuariosIds = StreamSupport
            .stream(usuarioRepository
                .findAll(usuarioLogadoRequest.toUsuarioPredicate()).spliterator(), false)
            .map(Usuario::getId)
            .collect(Collectors.toList());

        if (isEmpty(usuariosIds)) {
            usuarioLogadoRequest.getPeriodos()
                .forEach(periodo -> periodo.setTotalUsuariosLogados(0));
            return usuarioLogadoRequest.getPeriodos();
        }
        usuarioLogadoRequest.setUsuariosIds(usuariosIds);
        return notificacaoUsuarioAcessoService.countUsuariosLogadosPorPeriodo(usuarioLogadoRequest);
    }
}
