package br.com.xbrain.autenticacao.modules.autenticacao.service;

import br.com.xbrain.autenticacao.config.AuthServerConfig;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static com.google.common.collect.Lists.partition;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class AutenticacaoService {

    private static final String USUARIO_AUTENTICADO_KEY = "usuarioAutenticado";
    public static final String HEADER_USUARIO_EMULADOR = "X-Usuario-Emulador";
    public static final String HEADER_USUARIO_CANAL = "X-Usuario-Canal";
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Canal não encontrado.");
    private static final String MSG_USUARIO_NAO_ENCONTRADO = "O usuário %d não foi encontrado.";

    @Value("#{'${app-config.multiplo-login.emails}'.split(',')}")
    private List<String> emailsPermitidosComMultiplosLogins;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @Autowired
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;

    public static boolean hasAuthentication() {
        OAuth2Authentication authentication = getAuthentication();
        return authentication != null && authentication.getUserAuthentication() != null;
    }

    public static OAuth2Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof OAuth2Authentication ? (OAuth2Authentication) authentication : null;
    }

    public static Integer getUsuarioEmuladorId(HttpServletRequest request) {
        if (request.getHeader(HEADER_USUARIO_EMULADOR) != null) {
            return Integer.parseInt(request.getHeader(HEADER_USUARIO_EMULADOR));
        }
        return null;
    }

    public String getLoginUsuario() {
        return getAuthentication().getName();
    }

    public Integer getUsuarioId() {
        return Integer.parseInt(getAuthentication().getName().split(Pattern.quote("-"))[0]);
    }

    public Optional<Integer> getUsuarioAutenticadoId() {
        if (!hasAuthentication()) {
            return Optional.empty();
        }

        return Optional.of(getUsuarioId());
    }

    public UsuarioAutenticado getUsuarioAutenticado() {
        return loadUsuarioDataBase(getAuthentication());
    }

    public Optional<OAuth2AccessToken> getAccessToken() {
        return Optional.ofNullable(getAuthentication())
            .map(tokenStore::getAccessToken);
    }

    public void validarPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        var usuarioAutenticado = getUsuarioAutenticado();
        @SuppressWarnings("unchecked")
        var agentesAutorizados = Optional.ofNullable((List<Integer>) tokenStore.getAccessToken(getAuthentication())
            .getAdditionalInformation()
            .get("agentesAutorizados"))
            .orElseGet(() -> agenteAutorizadoNovoService.getAasPermitidos(getUsuarioId()));
        usuarioAutenticado.hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, agentesAutorizados);
    }

    public <T> Optional<T> getTokenProperty(String property, Class<T> type) {
        return Optional.ofNullable(tokenStore.getAccessToken(getAuthentication())
                .getAdditionalInformation()
                .get(property))
            .map(type::cast);
    }

    @SuppressWarnings("unchecked")
    private UsuarioAutenticado loadUsuarioDataBase(Authentication authentication) {
        LinkedHashMap details = (LinkedHashMap)
                ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

        return Optional.ofNullable(details.get(USUARIO_AUTENTICADO_KEY))
                .map(usuarioAutenticadoObj -> (UsuarioAutenticado)usuarioAutenticadoObj)
                .or(() -> usuarioRepository.findComplete(getUsuarioId())
                    .map(Usuario::forceLoad)
                    .map(usuario -> new UsuarioAutenticado(usuario, authentication.getAuthorities()))
                    .map(usuarioAutenticado -> {
                        details.putIfAbsent(USUARIO_AUTENTICADO_KEY, usuarioAutenticado);
                        return usuarioAutenticado;
                    }))
                .orElse(null);
    }

    public void logout(String login) {
        if (somenteUmLoginPorUsuario(login)) {
            tokenStore
                .findTokensByClientIdAndUserName(
                    AuthServerConfig.APP_CLIENT,
                    login)
                .forEach(token -> {
                    getUsuarioAutenticadoId().ifPresent(usuarioAcessoService::registrarLogout);
                    tokenStore.removeAccessToken(token);
                });
        }
    }

    public void logout(Integer usuarioId) {
        var usuario = buscarUsuario(usuarioId);
        logout(usuario.getLogin());
        forcarLogoutGeradorLeadsEClienteLojaFuturo(usuario);
    }

    public void logout(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                .forEach(this::deslogarUsuariosPorIds);
        }
    }

    private void deslogarUsuariosPorIds(List<Integer> usuariosIds) {
        usuarioRepository.findByIdIn(usuariosIds)
            .forEach(usuario -> {
                try {
                    logout(usuario.getLogin());
                } catch (Exception ex) {
                    log.error("Houve um erro ao deslogar o usuário: " + usuario.getId(), ex);
                }
            });
    }

    public void logoutAllUsers() {
        tokenStore
            .findTokensByClientId(AuthServerConfig.APP_CLIENT)
            .forEach(token -> {
                tokenStore.removeAccessToken(token);
                usuarioAcessoService.registrarLogout(getUsuarioIdFromToken(token));
            });
    }

    private Integer getUsuarioIdFromToken(OAuth2AccessToken token) {
        return (Integer) token.getAdditionalInformation().get("usuarioId");
    }

    public void forcarLogoutGeradorLeadsEClienteLojaFuturo(Usuario usuario) {
        if (usuario.isCargo(CodigoCargo.GERADOR_LEADS) || usuario.isCargo(CodigoCargo.CLIENTE_LOJA_FUTURO)) {
            tokenStore
                .findTokensByClientIdAndUserName(
                    AuthServerConfig.APP_CLIENT,
                    usuario.getLogin())
                .forEach(token -> tokenStore.removeAccessToken(token));
        }
    }

    public boolean isEmulacao() {
        return request.getAttribute("emulacao") != null;
    }

    public boolean somenteUmLoginPorUsuario(String login) {
        return !isUsuarioPermitidoMultiplosAcessos(login)
            && emailsPermitidosComMultiplosLogins
                .stream()
                .noneMatch(loginPermitido -> loginPermitido.equalsIgnoreCase(login.split(Pattern.quote("-"))[1]));
    }

    private boolean isUsuarioPermitidoMultiplosAcessos(String login) {
        return usuarioRepository.findComplete(Integer.valueOf(login.split(Pattern.quote("-"))[0]))
            .map(Usuario::isGeradorLeadsOuClienteLojaFuturo)
            .orElse(Boolean.FALSE);
    }

    public static Optional<ECanal> getUsuarioCanal(HttpServletRequest request) {
        return Optional.ofNullable(!isEmpty(request.getHeader(HEADER_USUARIO_CANAL))
            ? ECanal.valueOf(request.getHeader(HEADER_USUARIO_CANAL)) : null);
    }

    public ECanal getUsuarioCanal() {
        return AutenticacaoService.getUsuarioCanal(request).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public void logoutLoginMultiplo(Integer usuarioId) {
        forcarLogoutGeradorLeadsEClienteLojaFuturo(buscarUsuario(usuarioId));
    }

    private Usuario buscarUsuario(Integer usuarioId) {
        return usuarioRepository.findComplete(usuarioId)
            .orElseThrow(() -> new ValidacaoException(String.format(MSG_USUARIO_NAO_ENCONTRADO, usuarioId)));
    }
}
