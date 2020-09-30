package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;

@Service
@SuppressWarnings("MagicNumber")
@RequiredArgsConstructor
public class HorarioAcessoAtivoLocalService {

    private LocalTime horarioInicioSabado = LocalTime.of(10, 0, 0);
    private LocalTime horarioTerminoSabado = LocalTime.of(16, 0, 0);
    private LocalTime horarioInicioSemanal = LocalTime.of(9, 0, 0);
    private LocalTime horarioTerminoSemanal = LocalTime.of(21, 0, 0);
    public final AutenticacaoService autenticacaoService;
    public final DataHoraAtual dataHoraAtual;
    public final SiteService siteService;

    public boolean isDentroHorarioPermitido() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (usuarioAutenticado.isOperadorTelevendasAtivoLocal()) {
            var site = siteService.findById(usuarioAutenticado.getSiteId());
            var timeZone = ZoneId.of(site.getTimeZone().getZoneId());
            return isDentroHorarioPermitido(timeZone);
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
}
