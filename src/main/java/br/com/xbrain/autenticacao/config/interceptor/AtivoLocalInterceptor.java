package br.com.xbrain.autenticacao.config.interceptor;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.service.HorarioAcessoAtivoLocalService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
public class AtivoLocalInterceptor extends HandlerInterceptorAdapter {

    private static final UnauthorizedUserException FORA_HORARIO_PERMITIDO_EX =
        new UnauthorizedUserException("Fora do horário permitido.");

    @Autowired
    private SiteService siteService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CallService callService;
    @Autowired
    private HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        autenticacaoService.getAccessToken()
            .filter(this::isOperadorTelevendasAtivoLocal)
            .map(this::buscarSite)
            .map(Site::getTimeZone)
            .map(ETimeZone::getZoneId)
            .map(ZoneId::of)
            .filter(z -> !horarioAcessoAtivoLocalService.isDentroHorarioPermitido(z) && !isRamalEmUso())
            .ifPresent(error -> {
                throw FORA_HORARIO_PERMITIDO_EX;
            });
    }

    private boolean isOperadorTelevendasAtivoLocal(OAuth2AccessToken token) {
        var info = token.getAdditionalInformation();

        return info.containsValue(CodigoCargo.OPERACAO_TELEVENDAS)
            && info.containsValue(Set.of(ECanal.ATIVO_PROPRIO.name()));
    }

    private Site buscarSite(OAuth2AccessToken token) {
        return Optional.of(token.getAdditionalInformation())
            .map(info -> info.get("siteId"))
            .map(Integer.class::cast)
            .map(siteService::findById)
            .orElse(null);
    }

    private boolean isRamalEmUso() {
        return callService.consultarStatusUsoRamalByUsuarioAutenticado();
    }
}
