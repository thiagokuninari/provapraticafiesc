package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.notificacaoapi.service.NotificacaoApiService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
@SuppressWarnings("MagicNumber")
@RequiredArgsConstructor
public class HorarioAcessoAtivoLocalService {

    private static final UnauthorizedUserException FORA_HORARIO_PERMITIDO_EX =
            new UnauthorizedUserException("Fora do horário permitido.");

    private LocalTime horarioInicioSabado = LocalTime.of(10, 0, 0);
    private LocalTime horarioTerminoSabado = LocalTime.of(16, 0, 0);
    private LocalTime horarioInicioSemanal = LocalTime.of(9, 0, 0);
    private LocalTime horarioTerminoSemanal = LocalTime.of(21, 0, 0);

    public final AutenticacaoService autenticacaoService;
    public final DataHoraAtual dataHoraAtual;
    public final SiteService siteService;
    public final CallService callService;
    public final NotificacaoApiService notificacaoApiService;
    private final Environment environment;

    public void validarHorarioAcessoVendedor() {
        if (isTest() || AutenticacaoService.hasAuthentication()) {
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
            Optional.ofNullable(usuarioAutenticado)
                    .filter(UsuarioAutenticado::isOperadorTelevendasAtivoLocal)
                    .map(u -> u.getUsuario().getSite())
                    .map(Site::getTimeZone)
                    .map(ETimeZone::getZoneId)
                    .map(ZoneId::of)
                    .filter(z -> !isDentroHorarioPermitido(z) && !isDentroTabulacao() && !isRamalEmUso())
                    .ifPresent(error -> {
                        autenticacaoService.logout(autenticacaoService.getUsuarioId());
                        throw FORA_HORARIO_PERMITIDO_EX;
                    });
        }
    }

    @Transactional
    public boolean isDentroHorarioPermitido() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        return isDentroHorarioPermitido(usuarioAutenticado.getUsuario());
    }

    @Transactional
    public boolean isDentroHorarioPermitido(Usuario usuario) {
        if (usuario.isOperadorTelevendasAtivoLocal() && nonNull(usuario.getSite())) {
            var timeZone = usuario.getSite().getTimeZone();
            return isDentroHorarioPermitido(ZoneId.of(timeZone.getZoneId()));
        }
        return true;
    }

    public boolean isDentroHorarioPermitido(ZoneId timeZone) {
        var now = dataHoraAtual.getDataHora(timeZone);
        switch (now.getDayOfWeek()) {
            case SUNDAY:
                return false;
            case SATURDAY:
                return isDentroHorarioPermitidoNoSabado(now.toLocalTime());
            default:
                return isDentroHorarioPermitidoNaSemana(now.toLocalTime());
        }
    }

    private boolean isDentroHorarioPermitidoNoSabado(LocalTime horario) {
        return horario.isAfter(horarioInicioSabado)
            && horario.isBefore(horarioTerminoSabado);
    }

    private boolean isDentroHorarioPermitidoNaSemana(LocalTime horario) {
        return horario.isAfter(horarioInicioSemanal)
            && horario.isBefore(horarioTerminoSemanal);
    }

    public void alterarHorariosAcesso(LocalTime horarioInicioSabado, LocalTime horarioTerminoSabado,
                                      LocalTime horarioInicioSemanal, LocalTime horarioTerminoSemanal) {
        if (!autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            throw new PermissaoException();
        }
        this.horarioInicioSabado = horarioInicioSabado;
        this.horarioTerminoSabado = horarioTerminoSabado;
        this.horarioInicioSemanal = horarioInicioSemanal;
        this.horarioTerminoSemanal = horarioTerminoSemanal;
    }

    private boolean isDentroTabulacao() {
        var usuarioId = autenticacaoService.getUsuarioId();
        return notificacaoApiService.consultarStatusTabulacaoByUsuario(usuarioId);
    }

    private boolean isRamalEmUso() {
        return callService.consultarStatusUsoRamalByUsuarioAutenticado();
    }

    private boolean isTest() {
        return environment.acceptsProfiles("test");
    }
}
