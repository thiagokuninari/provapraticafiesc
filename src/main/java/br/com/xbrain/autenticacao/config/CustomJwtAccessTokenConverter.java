package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;
import static org.springframework.util.StringUtils.isEmpty;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter implements
        JwtAccessTokenConverterConfigurer {

    @Autowired
    private FuncionalidadeService funcionalidadeService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private EquipeVendasService equipeVendasService;
    @Autowired
    private EquipeVendaD2dService equipeVendaD2dService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        defaultOAuth2AccessToken.setAdditionalInformation(new LinkedHashMap<>(accessToken.getAdditionalInformation()));
        if (authentication.getUserAuthentication() != null) {
            User userAuth = (User) authentication.getUserAuthentication().getPrincipal();

            usuarioRepository
                    .findComplete(Integer.valueOf(userAuth.getUsername().split(Pattern.quote("-"))[0]))
                    .ifPresent(usuario -> setAdditionalInformation(
                            defaultOAuth2AccessToken,
                            usuario,
                            userAuth,
                            getAgentesAutorizadosPermitidos(usuario),
                            getEmpresasDoUsuario(usuario),
                            getEquipesSupervisionadas(usuario),
                            getEquipeVendas(usuario)));
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

    private List<Integer> getEquipesSupervisionadas(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
                ? equipeVendasService.getEquipesPorSupervisor(usuario.getId())
                .stream()
                .map(EquipeVendasSupervisionadasResponse::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    private List<EquipeVendaDto> getEquipeVendas(Usuario usuario) {
        if (usuario.getNivelCodigo() == AGENTE_AUTORIZADO) {
            EquipeVendaDto equipeVendas = equipeVendasService.getByUsuario(usuario.getId());
            return !ObjectUtils.isEmpty(equipeVendas)
                    ? Collections.singletonList(equipeVendas)
                    : Collections.emptyList();
        } else {
            return equipeVendaD2dService.getEquipeVendas(usuario.getId());
        }
    }

    private String getEstrutura(Usuario usuario) {
        return usuario.isAgenteAutorizado() ? agenteAutorizadoService.getEstrutura(usuario.getId()) : null;
    }

    private void setAdditionalInformation(OAuth2AccessToken token,
                                          Usuario usuario,
                                          User user,
                                          List<Integer> agentesAutorizados,
                                          List<Empresa> empresas,
                                          List<Integer> equipesSupervisionadas,
                                          List<EquipeVendaDto> equipeVendas) {
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
        token.getAdditionalInformation().put("canais", getCanais(usuario));
        token.getAdditionalInformation().put("equipeVendas", equipeVendas);
        token.getAdditionalInformation().put("organizacao", getOrganizacao(usuario));
        token.getAdditionalInformation().put("organizacaoId", getOrganizacaoId(usuario));

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
        token.getAdditionalInformation().put("aplicacoes",
                getAplicacoes(usuario));
        token.getAdditionalInformation().put("equipesSupervisionadas",
                equipesSupervisionadas);
        token.getAdditionalInformation().put("estruturaAa", getEstrutura(usuario));
    }

    private String getOrganizacao(Usuario usuario) {
        return !ObjectUtils.isEmpty(usuario.getOrganizacao()) ? usuario.getOrganizacao().getCodigo() : "";
    }

    private Integer getOrganizacaoId(Usuario usuario) {
        return Objects.nonNull(usuario.getOrganizacao()) ? usuario.getOrganizacao().getId() : null;
    }

    private List getListaEmpresaPorCampo(List<Empresa> empresas, Function<Empresa, Object> mapper) {
        return empresas
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    // TODO cachear getFuncionalidadesPermitidasAoUsuario
    private List getAplicacoes(Usuario user) {
        List<Funcionalidade> funcionalidade = funcionalidadeService.getFuncionalidadesPermitidasAoUsuario(user);
        return funcionalidade
                .stream()
                .filter(f -> f.getPermissaoTela() == null)
                .map(f -> f.getAplicacao().getCodigo())
                .distinct()
                .collect(Collectors.toList());
    }

    private Set<String> getCanais(Usuario usuario) {
        switch (usuario.getNivelCodigo()) {
            case XBRAIN:
            case MSO:
                return Sets.newHashSet(ECanal.AGENTE_AUTORIZADO.name(), ECanal.D2D_PROPRIO.name());
            case OPERACAO:
                return ObjectUtils.isEmpty(usuario.getCanais()) ? Sets.newHashSet() : usuario.getCanaisString();
            default:
                return Sets.newHashSet(ECanal.AGENTE_AUTORIZADO.name());
        }
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }

}