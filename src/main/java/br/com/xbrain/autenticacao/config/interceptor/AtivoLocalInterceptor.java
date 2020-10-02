package br.com.xbrain.autenticacao.config.interceptor;

import br.com.xbrain.autenticacao.modules.comum.service.HorarioAcessoAtivoLocalService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@AllArgsConstructor
@NoArgsConstructor
public class AtivoLocalInterceptor extends HandlerInterceptorAdapter {

    private static final String URL_OAUTH_TOKEN = "/oauth/token";
    @Autowired
    private HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!request.getRequestURI().contains(URL_OAUTH_TOKEN)) {
            horarioAcessoAtivoLocalService.validarHorarioAcessoVendedor();
        }
    }

}
