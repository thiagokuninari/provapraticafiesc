package br.com.xbrain.autenticacao.modules.logrequest.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
public class LogRequestInterceptor implements HandlerInterceptor {

    @Autowired
    private LogRequestService service;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        if (AutenticacaoService.hasAuthentication()) {
            String id = AutenticacaoService.getAuthentication().getName().split(Pattern.quote("-"))[0];
            int usuarioId = Integer.valueOf(id);
            String usuarioEmail = AutenticacaoService.getAuthentication().getName().split(Pattern.quote("-"))[1];
            service.saveAsync(
                    request.getRequestURI(),
                    request.getMethod(),
                    usuarioId,
                    usuarioEmail,
                    AutenticacaoService.getUsuarioEmuladorId(request),
                    request.getRemoteAddr()
            );
        }
    }
}
