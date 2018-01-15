package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping(value = "api/emular")
public class EmularUsuarioController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Value("${app-config.oauth-client}")
    private String oauthClient;
    @Value("${app-config.oauth-client-secret}")
    private String oauthClientSecret;

    private HashMap<String, String> getParameters(Usuario usuario) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("client_id", oauthClient);
        parameters.put("client_secret", oauthClientSecret);
        parameters.put("grant_type", "password");
        parameters.put("scope", "app");
        parameters.put("password", "");
        parameters.put("username", usuario.getEmail());
        return parameters;
    }

    @RequestMapping(value = "usuario", method = RequestMethod.GET)
    public ResponseEntity<OAuth2AccessToken> emularUsuario(Principal principal, Integer id) throws Exception {
        validarPropriaEmulacao(id);
        validarReemulacao();
        request.setAttribute("emulacao", true);
        return tokenEndpoint.postAccessToken(principal, getParameters(usuarioService.findById(id)));
    }

    private void validarReemulacao() {
        if (request.getHeader(AutenticacaoService.HEADER_USUARIO_EMULADOR) != null) {
            throw new ValidacaoException("Já existe uma emulação em execução! Encerre a atual para iniciar uma outra.");
        }
    }

    private void validarPropriaEmulacao(Integer id) {
        if (autenticacaoService.getUsuarioId().equals(id)) {
            throw new ValidacaoException("Não é possível realizar a emulação do seu próprio usuário.");
        }
    }
}
