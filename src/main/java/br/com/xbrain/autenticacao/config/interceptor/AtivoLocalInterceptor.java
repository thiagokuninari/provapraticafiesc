package br.com.xbrain.autenticacao.config.interceptor;

import br.com.xbrain.autenticacao.modules.comum.service.HorarioAcessoAtivoLocalService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


@AllArgsConstructor
@NoArgsConstructor
public class AtivoLocalInterceptor extends HandlerInterceptorAdapter {

    private static final String[] URI_NOT_ALLOWED = {
        "/oauth/token",
        "/remover-ramal-configuracao"
    };


    @Autowired
    private HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (isUriAllowed(request)) {
            horarioAcessoAtivoLocalService.validarHorarioAcessoVendedor();
        }
    }

    private boolean isUriAllowed(HttpServletRequest request) {
        return Arrays.stream(URI_NOT_ALLOWED).noneMatch(uri -> request.getRequestURI().contains(uri));
    }

}
