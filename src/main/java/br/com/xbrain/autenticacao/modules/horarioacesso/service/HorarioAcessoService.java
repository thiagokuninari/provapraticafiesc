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
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class HorarioAcessoService {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");
    public static final UnauthorizedUserException ACESSO_FORA_HORARIO_PERMITIDO =
        new UnauthorizedUserException("Usuário fora do horário permitido.");

    private final Environment environment;

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private HorarioAtuacaoRepository atuacaoRepository;
    @Autowired
    private HorarioHistoricoRepository historicoRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Autowired
    private CallService callService;
    @Autowired
    private NotificacaoApiService notificacaoApiService;

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

    private void desreferenciaHorarioAtuacao(HorarioAcesso horarioAtuacao) {
        var horariosAtuacao = atuacaoRepository.findByHorarioAcessoId(horarioAtuacao.getId());
        horariosAtuacao.forEach(atuacao -> atuacao.setHorarioAcesso(null));
        horariosAtuacao.forEach(atuacao -> atuacaoRepository.save(atuacao));
    }

    private void validarSite(Integer siteId) {
        var horarios = repository.findBySiteId(siteId);
        if (nonNull(horarios)) {
            throw new ValidacaoException("Site já possui horário de acesso cadastrado.");
        }
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

    public boolean getStatus() {
        var usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();

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

    public boolean getStatus(Integer siteId) {
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
        if (isTest() || AutenticacaoService.hasAuthentication() && isOperadorTelevendasAtivoLocalByTokenStore()) {
            var horarioAtual = dataHoraAtual.getDataHora();
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
            Optional.ofNullable(usuarioAutenticado)
                .filter(UsuarioAutenticado::isOperadorTelevendasAtivoLocal)
                .map(usuario -> getSiteByUsuario(usuario.getUsuario()))
                .map(site -> repository.findBySiteId(site.getId())
                    .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO))
                .map(horarioAcesso -> atuacaoRepository
                    .findByHorarioAcessoId(horarioAcesso.getId()))
                .map(horariosAtuacao -> horariosAtuacao.stream().filter(h -> 
                    h.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual))).findAny().orElse(null))
                .filter(horario -> 
                    isNull(horario)
                    || !isHorarioAtuacaoPermitido(getHoraAtual(horarioAtual), horario)
                    && !isDentroTabulacao()
                    && !isRamalEmUso())
                .ifPresent(error -> {
                    callService.liberarRamalUsuarioAutenticado();
                    autenticacaoService.logout(autenticacaoService.getUsuarioId());
                    throw ACESSO_FORA_HORARIO_PERMITIDO;
                });
        }
    }

    public boolean isDentroHorarioPermitido(Usuario usuario) {
        var horarioAtual = dataHoraAtual.getDataHora();
        if (usuario.isOperadorTelevendasAtivoLocal()) {
            var horario = Optional.ofNullable(usuario)
                .map(user -> getSiteByUsuario(user))
                .map(site -> repository.findBySiteId(site.getId())
                    .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO))
                .map(horarioAcesso -> atuacaoRepository.findByHorarioAcessoId(horarioAcesso.getId()))
                .map(horariosAtuacao -> horariosAtuacao.stream().filter(h -> 
                    h.getDiaSemana().equals(EDiaSemana.valueOf(horarioAtual))).findAny().orElse(null))
                .orElse(null);
            return nonNull(horario) && isHorarioAtuacaoPermitido(getHoraAtual(horarioAtual), horario);
        }
        return true;
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

    public boolean isOperadorTelevendasAtivoLocalByTokenStore() {
        return autenticacaoService.getAccessToken()
            .filter(token -> {
                var info = token.getAdditionalInformation();
                return info.containsValue(CodigoCargo.OPERACAO_TELEVENDAS)
                    && info.containsValue(Set.of(ECanal.ATIVO_PROPRIO.name()));
            }).isPresent();
    }
}
