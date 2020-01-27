package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService.HEADER_USUARIO_EMULADOR;

@Component
public class CustomTokenEndpointAuthenticationFilter extends GenericFilterBean implements Filter {

    private static final String URL_OAUTH_TOKEN = "/oauth/token";
    private static final String CONTENT_TYPE_TOKEN_REQUEST = "multipart/form-data";

    @Autowired
    private EquipeVendaService equipeVendaService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        if (isValidTokenRequest(servletRequest) && verificarPausaCasoPossuaUsername(servletRequest)) {
            sendErrorValidation(servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean verificarPausaCasoPossuaUsername(ServletRequest servletRequest) {
        var username = servletRequest.getParameter("username");
        return Objects.nonNull(username) && equipeVendaService.verificaPausaEmAndamento(username);
    }

    private boolean isValidTokenRequest(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        return !(servletRequest instanceof SecurityContextHolderAwareRequestWrapper)
            && request.getRequestURI().contains(URL_OAUTH_TOKEN)
            && request.getContentType().contains(CONTENT_TYPE_TOKEN_REQUEST);
    }

    private void sendErrorValidation(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Origin, X-Requested-With, Content-Type, Accept, X-Usuario-Canal, " + HEADER_USUARIO_EMULADOR);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Acesso negado. Usuário com pausa agendada em andamento.");
    }
}
