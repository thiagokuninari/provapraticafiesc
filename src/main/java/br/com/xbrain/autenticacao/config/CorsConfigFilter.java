package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService.HEADER_USUARIO_EMULADOR;

@Component
public class CorsConfigFilter implements Filter {

    @Value("#{'${app-config.url-origin-cors}'.split(',')}")
    private List<String> urlsOriginCors;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        var requestOrigin = request.getHeader("Origin");
        if (requestOrigin != null && urlsOriginCors.stream().anyMatch(requestOrigin::contains)) {
            response.setHeader("Access-Control-Allow-Origin", requestOrigin);
        }

        setHeaders(response);
        filterChain.doFilter(servletRequest, response);
    }

    private void setHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
            "Authorization, Origin, X-Requested-With, Content-Type, Accept, X-Usuario-Canal, " + HEADER_USUARIO_EMULADOR);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
