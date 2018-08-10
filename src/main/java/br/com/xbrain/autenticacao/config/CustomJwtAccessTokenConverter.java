package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter implements
        JwtAccessTokenConverterConfigurer {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        defaultOAuth2AccessToken.setAdditionalInformation(new LinkedHashMap<>(accessToken.getAdditionalInformation()));
        if (authentication.getUserAuthentication() != null) {
            User userAuth = (User) authentication.getUserAuthentication().getPrincipal();

            usuarioRepository
                    .findComplete(new Integer(userAuth.getUsername().split(Pattern.quote("-"))[0]))
                    .ifPresent(usuario -> setAdditionalInformation(
                            defaultOAuth2AccessToken,
                            usuario,
                            userAuth,
                            getAgentesAutorizadosPermitidos(usuario)));
        }

        return defaultOAuth2AccessToken;
    }

    private List<Integer> getAgentesAutorizadosPermitidos(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
                ? agenteAutorizadoService.getAasPermitidos(usuario.getId())
                : Collections.emptyList();
    }

    private void setAdditionalInformation(OAuth2AccessToken token, Usuario usuario, User user, List<Integer> agentesAutorizados) {
        token.getAdditionalInformation().put("usuarioId", usuario.getId());
        token.getAdditionalInformation().put("cpf", usuario.getCpf());
        token.getAdditionalInformation().put("email", usuario.getEmail());
        token.getAdditionalInformation().put("login", user.getUsername());
        token.getAdditionalInformation().put("nome", usuario.getNome());
        token.getAdditionalInformation().put("nomeAbreviado", StringUtil.getNomeAbreviado(usuario.getNome()));
        token.getAdditionalInformation().put("alterarSenha", usuario.getAlterarSenha());
        token.getAdditionalInformation().put("nivel", usuario.getCargo().getNivel().getNome());
        token.getAdditionalInformation().put("nivelCodigo", usuario.getCargo().getNivel().getCodigo());
        token.getAdditionalInformation().put("departamento", usuario.getDepartamento().getNome());
        token.getAdditionalInformation().put("departamentoCodigo", usuario.getDepartamento().getCodigo());
        token.getAdditionalInformation().put("cargo", usuario.getCargo().getNome());
        token.getAdditionalInformation().put("cargoCodigo", usuario.getCargo().getCodigo());
        token.getAdditionalInformation().put("cargoId", usuario.getCargoId());
        token.getAdditionalInformation().put("nivelId", usuario.getNivelId());
        token.getAdditionalInformation().put("departamentoId", usuario.getDepartamentoId());
        token.getAdditionalInformation().put("empresas", usuario.getEmpresasId());
        token.getAdditionalInformation().put("empresasNome", usuario.getEmpresasNome());
        token.getAdditionalInformation().put("unidadesNegocios", usuario.getUnidadesNegociosId());
        token.getAdditionalInformation().put("agentesAutorizados", agentesAutorizados);
        token.getAdditionalInformation().put("active", true);
        token.getAdditionalInformation().put("authorities",
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray());
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }
}
