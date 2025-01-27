package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.claroindico.service.ClaroIndicoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.ColaboradorInativacaoPolRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECodigoObservacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarUsuarioFeederMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.CARGOS_COLABORADOR_BKO_CENTRALIZADO;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class UsuarioAcessoService {

    private static final String MSG_ERRO_AO_INATIVAR_USUARIO = "ocorreu um erro desconhecido ao inativar "
        + "usuários que estão a mais de 32 dias sem efetuar login.";
    private static final String MSG_ERRO_AO_DELETAR_REGISTROS = "Ocorreu um erro desconhecido ao tentar deletar "
        + " os registros antigos da tabela usuário acesso.";

    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService autenticacaoService;
    private final UsuarioHistoricoService usuarioHistoricoService;
    private final UsuarioAcessoRepository usuarioAcessoRepository;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final InativarColaboradorMqSender inativarColaboradorMqSender;
    private final NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;
    private final InativarUsuarioFeederMqSender inativarUsuarioFeederMqSender;
    private final ClaroIndicoService claroIndicoService;

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
                    if (usuario.getNivelCodigo() == CodigoNivel.FEEDER) {
                        inativarUsuarioFeeder(usuario);
                    } else if (usuario.getNivelCodigo() == CodigoNivel.BACKOFFICE_CENTRALIZADO) {
                        inativarUsuarioBkoCentralizado(usuario);
                    } else {
                        inativarColaboradorPol(usuario);
                    }
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

    @Transactional
    public long deletarHistoricoUsuarioAcessoAgendador() {
        return usuarioAcessoRepository.deletarHistoricoUsuarioAcesso();
    }

    public Page<UsuarioAcessoResponse> getAll(PageRequest pageRequest, UsuarioAcessoFiltros usuarioAcessoFiltros) {
        aplicarFiltros(usuarioAcessoFiltros);

        var lista = stream(usuarioAcessoRepository
            .findAll(usuarioAcessoFiltros.toPredicate(), pageRequest).spliterator(), false)
            .map(UsuarioAcessoResponse::of)
            .distinct()
            .collect(Collectors.toList());
        return new PageImpl<>(lista, pageRequest, getCountDistinct(usuarioAcessoFiltros));
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

        return stream(usuarioAcessoRepository
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

    public List<UsuarioLogadoResponse> getUsuariosLogadosCompletos(UsuarioLogadoRequest request) {
        var usuarios = stream(usuarioRepository.findAll(request.toUsuarioPredicate()).spliterator(), false)
            .collect(Collectors.toList());

        return !usuarios.isEmpty()
            ? getOperadoresLogados(usuarios)
            : List.of();
    }

    private List<UsuarioLogadoResponse> getOperadoresLogados(List<Usuario> usuarios) {
        var usuariosIds = usuarios.stream().map(Usuario::getId).collect(Collectors.toList());

        return notificacaoUsuarioAcessoService.getUsuariosLogadosComDataEntradaPorIds(usuariosIds)
            .stream()
            .peek(usuarioLogado -> alterarDadosResponse(usuarios, usuarioLogado))
            .collect(Collectors.toList());
    }

    private void alterarDadosResponse(List<Usuario> usuarios, UsuarioLogadoResponse usuarioLogado) {
        usuarios.stream()
            .filter(usuario -> usuarioLogado.getUsuarioId().equals(usuario.getId()))
            .forEach(usuarioLogado::setDadosResponse);
    }

    public void aplicarAgenteAutorizadoFiltro(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        if (!isEmpty(usuarioAcessoFiltros.getAaId())) {
            usuarioAcessoFiltros.setAgenteAutorizadosIds(getIdUsuariosByAaId(usuarioAcessoFiltros));
        }
    }

    private List<Integer> obterUsuariosIds(UsuarioLogadoRequest request) {
        return stream(
            usuarioRepository.findAll(request.toUsuarioPredicate()).spliterator(), false)
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    private void aplicarFiltros(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        aplicarAgenteAutorizadoFiltro(usuarioAcessoFiltros);
    }

    private long getCountDistinct(UsuarioAcessoFiltros usuarioAcessoFiltros) {
        return stream(usuarioAcessoRepository
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

    private void inativarUsuarioFeeder(UsuarioDto usuario) {
        if (usuario.getEmail() != null) {
            inativarUsuarioFeederMqSender.sendSuccess(usuario.getEmail());
        } else {
            log.warn("Usuário " + usuario.getId() + " não possui um email cadastrado.");
        }
    }

    private void inativarUsuarioBkoCentralizado(UsuarioDto usuario) {
        if (CARGOS_COLABORADOR_BKO_CENTRALIZADO.contains(usuario.getCargoCodigo())) {
            claroIndicoService.desvincularUsuarioDaFilaTratamentoInativacao(usuario.getId());
        }
    }

    private void usuarioIsXbrain() {
        if (!autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            throw new PermissaoException();
        }
    }
}
