package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.ColaboradorInativacaoPolRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECodigoObservacao;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    @Lazy
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Value("${app-config.timer-usuario.data-hora-inativar-usuario-a-partir-de}")
    private String dataHoraInativarUsuario;

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
            var usuarios = buscarUsuariosParaInativar(LocalDateTime.parse(dataHoraInativarUsuario))
                .stream()
                .peek(usuario -> {
                    usuarioRepository.atualizarParaSituacaoInativo(usuario.getId());
                    usuarioHistoricoService.gerarHistoricoInativacao(usuario.getId(), origem);
                    inativarColaboradorPol(usuario);
                })
                .collect(Collectors.toList());

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

    private List<UsuarioDto> buscarUsuariosParaInativar(LocalDateTime dataHoraInativarUsuario) {
        var usuariosUltimoAcessoExpirado =
            CollectionUtils.emptyIfNull(
                getUsuariosUltimoAcessoExpiradoAndNotViabilidade(dataHoraInativarUsuario));
        var usuariosSemUltimoAcesso =
            CollectionUtils.emptyIfNull(
                getUsuariosSemUltimoAcessoAndNotViabilidade(dataHoraInativarUsuario));

        return Stream.concat(usuariosUltimoAcessoExpirado.stream(), usuariosSemUltimoAcesso.stream())
            .collect(Collectors.toList());

    }

    private List<UsuarioDto> getUsuariosSemUltimoAcessoAndNotViabilidade(LocalDateTime dataHoraInativarUsuario) {
        return usuarioRepository
            .findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(
                dataHoraInativarUsuario);
    }

    private List<UsuarioDto> getUsuariosUltimoAcessoExpiradoAndNotViabilidade(LocalDateTime dataHoraInativarUsuario) {
        return usuarioRepository
            .findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(
                dataHoraInativarUsuario);
    }

    private void inativarColaboradorPol(UsuarioDto usuario) {
        if (usuario.getEmail() != null) {
            var colaboradorInativacao = ColaboradorInativacaoPolRequest.of(usuario.getEmail(), ECodigoObservacao.IFA);
            inativarColaboradorMqSender.sendSuccess(colaboradorInativacao);
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
        aplicarFiltros(usuarioAcessoFiltros);

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
        return agenteAutorizadoService.getUsuariosByAaId(usuarioAcessoFiltros.getAaId(), false)
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
        aplicarFiltros(usuarioAcessoFiltros);

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
        var usuariosIds = obterUsuariosIds(usuarioLogadoRequest);

        if (isEmpty(usuariosIds)) {
            usuarioLogadoRequest.getPeriodos()
                .forEach(periodo -> periodo.setTotalUsuariosLogados(0));
            return usuarioLogadoRequest.getPeriodos();
        }
        usuarioLogadoRequest.setUsuariosIds(usuariosIds);
        return notificacaoUsuarioAcessoService.countUsuariosLogadosPorPeriodo(usuarioLogadoRequest);
    }

    public List<Integer> getUsuariosLogadosAtualPorIds(UsuarioLogadoRequest request) {
        return notificacaoUsuarioAcessoService.getUsuariosLogadosAtualPorIds(obterUsuariosIds(request));
    }

    private List<Integer> obterUsuariosIds(UsuarioLogadoRequest request) {
        return StreamSupport.stream(
                usuarioRepository.findAll(request.toUsuarioPredicate()).spliterator(), false)
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    private void aplicarFiltros(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        aplicarAgenteAutorizadoFiltro(usuarioAcessoFiltros);
    }

    public void aplicarAgenteAutorizadoFiltro(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        if (!isEmpty(usuarioAcessoFiltros.getAaId())) {
            usuarioAcessoFiltros.setAgenteAutorizadosIds(getIdUsuariosByAaId(usuarioAcessoFiltros));
        }
    }
}
