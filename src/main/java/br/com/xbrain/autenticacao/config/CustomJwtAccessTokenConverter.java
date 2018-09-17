package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;
import static org.springframework.util.StringUtils.isEmpty;

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
                            getAgentesAutorizadosPermitidos(usuario),
                            getEmpresasDoUsuario(usuario)));
        } else {
            defaultOAuth2AccessToken.getAdditionalInformation().put("active", true);
        }

        return defaultOAuth2AccessToken;
    }

    private List<Integer> getAgentesAutorizadosPermitidos(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
                ? agenteAutorizadoService.getAasPermitidos(usuario.getId())
                : Collections.emptyList();
    }

    private List<Empresa> getEmpresasDoUsuario(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
                ? agenteAutorizadoService.getEmpresasPermitidas(usuario.getId())
                : usuario.getEmpresas();

    }

    private void setAdditionalInformation(OAuth2AccessToken token,
                                          Usuario usuario,
                                          User user,
                                          List<Integer> agentesAutorizados,
                                          List<Empresa> empresas) {
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

        if (!isEmpty(empresas)) {
            token.getAdditionalInformation()
                    .put("empresas", getListaEmpresaPorCampo(empresas, Empresa::getId));

            token.getAdditionalInformation()
                    .put("empresasNome", getListaEmpresaPorCampo(empresas, Empresa::getNome));

            token.getAdditionalInformation()
                    .put("empresasCodigo", getListaEmpresaPorCampo(empresas, Empresa::getCodigo));
        }

        token.getAdditionalInformation().put("unidadesNegocios", usuario.getUnidadesNegociosId());
        token.getAdditionalInformation().put("agentesAutorizados", agentesAutorizados);
        token.getAdditionalInformation().put("active", true);
        token.getAdditionalInformation().put("authorities",
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray());
    }

    private List getListaEmpresaPorCampo(List<Empresa> empresas, Function<Empresa, Object> mapper) {
        return empresas
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }

}
