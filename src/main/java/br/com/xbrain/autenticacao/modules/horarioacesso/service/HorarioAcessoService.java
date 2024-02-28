package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.notificacaoapi.service.NotificacaoApiService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.ATIVO_PROPRIO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class HorarioAcessoService {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");
    public static final ValidacaoException ACESSO_FORA_HORARIO_PERMITIDO =
        new ValidacaoException("Usuário fora do horário permitido.");
    public static final UnauthorizedUserException USUARIO_FORA_HORARIO_PERMITIDO =
        new UnauthorizedUserException("Usuário fora do horário permitido.");
    public static final ValidacaoException CANAL_INVALIDO =
        new ValidacaoException("O canal informado não é válido.");
    public static final ValidacaoException USUARIO_SEM_CANAL_VALIDO =
        new ValidacaoException("Usuário não possui o canal válido.");

    private final Environment environment;
    private final HorarioAcessoRepository repository;
    private final HorarioAtuacaoRepository atuacaoRepository;
    private final HorarioHistoricoRepository historicoRepository;
    private final AutenticacaoService autenticacaoService;
    private final SiteService siteService;
    private final DataHoraAtual dataHoraAtual;
    private final CallService callService;
    private final NotificacaoApiService notificacaoApiService;

    public Page<HorarioAcessoResponse> getHorariosAcesso(PageRequest pageable, HorarioAcessoFiltros filtros) {
        var horariosAcesso = repository.findAll(filtros.toPredicate().build(), pageable)
            .map(HorarioAcessoResponse::of);
        horariosAcesso.getContent().forEach(horario -> horario.setHorariosAtuacao(
            atuacaoRepository.findByHorarioAcessoId(horario.getHorarioAcessoId())));
        return horariosAcesso;
    }

    public Page<HorarioAcessoResponse> getHistoricos(PageRequest pageable, Integer horarioAcessoId) {
        var horariosHistorico = historicoRepository.findByHorarioAcessoId(horarioAcessoId, pageable)
            .map(HorarioAcessoResponse::of);
        horariosHistorico.getContent().forEach(historico -> historico.setHorariosAtuacao(
            atuacaoRepository.findByHorarioHistoricoId(historico.getHorarioHistoricoId())));
        return horariosHistorico;
    }

    public HorarioAcessoResponse getHorarioAcesso(Integer id) {
        var horarioAcesso = HorarioAcessoResponse.of(repository.findById(id)
            .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO));
        horarioAcesso.setHorariosAtuacao(atuacaoRepository
            .findByHorarioAcessoId(horarioAcesso.getHorarioAcessoId()));
        return horarioAcesso;
    }

    public HorarioAcesso save(HorarioAcessoRequest request) {
        HorarioAcesso horarioAcesso = new HorarioAcesso();

        if (isNull(request.getId())) {
            validarSite(request.getSiteId());
            horarioAcesso = HorarioAcesso.of(request);
        } else {
            horarioAcesso = repository.findById(request.getId())
                .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
            desreferenciaHorarioAtuacao(horarioAcesso);
        }
        horarioAcesso.setDadosAlteracao(autenticacaoService.getUsuarioAutenticado().getUsuario());

        horarioAcesso = repository.save(horarioAcesso);

        var historico = HorarioHistorico.of(horarioAcesso);
        historico = historicoRepository.save(historico);

        criaHorariosAcesso(request.getHorariosAtuacao()
                .stream()
                .map(HorarioAtuacao::of)
                .collect(Collectors.toList()),
            horarioAcesso,
            historico);

        return horarioAcesso;
    }

    public void criaHorariosAcesso(List<HorarioAtuacao> horariosAtuacao,
                                    HorarioAcesso horarioAcesso,
                                    HorarioHistorico horarioHistorico) {
        try {
            horariosAtuacao.forEach(atuacao -> {
                atuacao.setHorarioAcesso(horarioAcesso);
                atuacao.setHorarioHistorico(horarioHistorico);
                atuacaoRepository.save(atuacao);
            });
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean getStatus(ECanal canal) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarCanal(canal);
        validarCanalUsuario(usuarioAutenticado);
        var usuario = usuarioAutenticado.getUsuario();
        if (usuario.isOperadorTelevendasAtivoLocal()) {
            var site = getSiteByUsuario(usuario);
            var horarioAcesso = repository.findBySiteId(site.getId())
                .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
            var horariosAtuacao = atuacaoRepository.findByHorarioAcessoId(horarioAcesso.getId());
            var horarioAtual = dataHoraAtual.getDataHora();
            var horario = horariosAtuacao.stream().filter(horarioAtuacao ->
                horarioAtuacao.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual)))
                .findAny().orElse(null);
            if (nonNull(horario)) {
                var horaAtual = LocalTime.of(horarioAtual.getHour(), horarioAtual.getMinute());
                return horaAtual.isAfter(horario.getHorarioInicio())
                    && horaAtual.isBefore(horario.getHorarioFim());
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean getStatus(ECanal canal, Integer siteId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarCanal(canal);
        validarCanalUsuario(usuarioAutenticado);
        var horarioAcesso = repository.findBySiteId(siteId)
            .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
        var horariosAtuacao = atuacaoRepository.findByHorarioAcessoId(horarioAcesso.getId());
        var horarioAtual = dataHoraAtual.getDataHora();
        var horario = horariosAtuacao.stream().filter(horarioAtuacao ->
            horarioAtuacao.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual)))
            .findAny().orElse(null);
        if (nonNull(horario)) {
            var horaAtual = LocalTime.of(horarioAtual.getHour(), horarioAtual.getMinute());
            return horaAtual.isAfter(horario.getHorarioInicio())
                && horaAtual.isBefore(horario.getHorarioFim());
        } else {
            return false;
        }
    }

    public void isDentroHorarioPermitido() {
        if (isTest() || AutenticacaoService.hasAuthentication()) {
            var horarioAtual = dataHoraAtual.getDataHora();
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
            if (usuarioAutenticado.isOperadorTelevendasAtivoLocal()) {
                Optional.ofNullable(usuarioAutenticado)
                    .map(usuario -> getSiteByUsuario(usuario.getUsuario()))
                    .map(site -> repository.findBySiteId(site.getId())
                        .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO))
                    .map(horarioAcesso -> atuacaoRepository
                        .findByHorarioAcessoId(horarioAcesso.getId()))
                    .map(horariosAtuacao -> horariosAtuacao.stream().filter(h ->
                        h.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual)))
                            .findAny().orElse(null))
                    .ifPresentOrElse(
                        horario -> {
                            if (!isHorarioAtuacaoPermitido(getHoraAtual(horarioAtual), horario)
                                && !isDentroTabulacao()
                                && !isRamalEmUso()) {
                                callService.liberarRamalUsuarioAutenticado();
                                autenticacaoService.logout(autenticacaoService.getUsuarioId());
                                throw USUARIO_FORA_HORARIO_PERMITIDO;
                            }
                        }, () -> {
                            throw USUARIO_FORA_HORARIO_PERMITIDO;
                        });
            }
        }
    }

    public void isDentroHorarioPermitido(Usuario usuario) {
        var horarioAtual = dataHoraAtual.getDataHora();
        if (usuario.isOperadorTelevendasAtivoLocal()) {
            Optional.ofNullable(usuario)
                .map(user -> getSiteByUsuario(user))
                .map(site -> repository.findBySiteId(site.getId())
                    .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO))
                .map(horarioAcesso -> atuacaoRepository.findByHorarioAcessoId(horarioAcesso.getId()))
                .map(horariosAtuacao -> horariosAtuacao.stream().filter(h ->
                    h.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual))).findAny().orElse(null))
                .ifPresentOrElse(
                    horario -> {
                        if (!isHorarioAtuacaoPermitido(getHoraAtual(horarioAtual), horario)) {
                            throw ACESSO_FORA_HORARIO_PERMITIDO;
                        }
                    }, () -> {
                        throw ACESSO_FORA_HORARIO_PERMITIDO;
                    });
        }
    }

    private Site getSiteByUsuario(Usuario usuario) {
        return siteService.getSitesPorPermissao(usuario).stream()
            .map(SelectResponse::getValue)
            .findFirst()
            .map(value -> siteService.findById((Integer) value))
            .orElse(null);
    }

    private LocalTime getHoraAtual(LocalDateTime horarioAtual) {
        return LocalTime.of(horarioAtual.getHour(), horarioAtual.getMinute());
    }

    private boolean isHorarioAtuacaoPermitido(LocalTime horaAtual, HorarioAtuacao horarioAtuacao) {
        return horaAtual.isAfter(horarioAtuacao.getHorarioInicio())
            && horaAtual.isBefore(horarioAtuacao.getHorarioFim());
    }

    private boolean isRamalEmUso() {
        return callService.consultarStatusUsoRamalByUsuarioAutenticado();
    }

    private boolean isDentroTabulacao() {
        var usuarioId = autenticacaoService.getUsuarioId();
        return notificacaoApiService.consultarStatusTabulacaoByUsuario(usuarioId);
    }

    private boolean isTest() {
        return environment.acceptsProfiles("test");
    }

    private void validarCanal(ECanal canal) {
        if (canal != ATIVO_PROPRIO) {
            throw CANAL_INVALIDO;
        }
    }

    private void validarCanalUsuario(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.isXbrainOuMso() && !usuarioAutenticado.getCanais().contains(ATIVO_PROPRIO)) {
            throw USUARIO_SEM_CANAL_VALIDO;
        }
    }

    private void desreferenciaHorarioAtuacao(HorarioAcesso horarioAtuacao) {
        var horariosAtuacao = atuacaoRepository.findByHorarioAcessoId(horarioAtuacao.getId());
        horariosAtuacao.forEach(atuacao -> atuacao.setHorarioAcesso(null));
        horariosAtuacao.forEach(atuacao -> atuacaoRepository.save(atuacao));
    }

    private void validarSite(Integer siteId) {
        if (repository.existsBySiteId(siteId)) {
            throw new ValidacaoException("Site já possui horário de acesso cadastrado.");
        }
    }
}
