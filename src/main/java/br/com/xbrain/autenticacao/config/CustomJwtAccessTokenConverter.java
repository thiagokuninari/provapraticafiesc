package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeTecnicaService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
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
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.ATIVO_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;
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
    @Autowired
    private SiteService siteService;
    @Autowired
    private SubCanalService subCanalService;

    @Autowired
    private EquipeTecnicaService equipeTecnicaService;

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
                    getEquipesTecnicasSupervisionadas(usuario),
                    getEquipeVendas(usuario),
                    getSites(usuario)));
        } else {
            defaultOAuth2AccessToken.getAdditionalInformation().put("active", true);
        }

        return defaultOAuth2AccessToken;
    }

    private List<SelectResponse> getSites(Usuario usuario) {
        return List.of(CodigoNivel.MSO, CodigoNivel.XBRAIN).contains(usuario.getNivelCodigo())
            || usuario.getCanais().contains(ATIVO_PROPRIO)
            ? siteService.getSitesPorPermissao(usuario)
            : Collections.emptyList();
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
        return usuario.isAgenteAutorizado()
            ? equipeVendasService.getEquipesPorSupervisor(usuario.getId())
            .stream()
            .map(EquipeVendasSupervisionadasResponse::getId)
            .collect(Collectors.toList())
            : Collections.emptyList();
    }

    private List<Integer> getEquipesTecnicasSupervisionadas(Usuario usuario) {
        return usuario.isAgenteAutorizado()
            ? equipeTecnicaService.getEquipesPorSupervisor(usuario.getId())
            .stream()
            .map(EquipeTecnicaSupervisionadasResponse::getId)
            .collect(Collectors.toList())
            : Collections.emptyList();
    }

    private List<EquipeVendaDto> getEquipeVendas(Usuario usuario) {
        if (usuario.isAgenteAutorizado()) {
            var equipeVendas = equipeVendasService.getByUsuario(usuario.getId());
            return !ObjectUtils.isEmpty(equipeVendas)
                ? Collections.singletonList(equipeVendas)
                : Collections.emptyList();
        } else {
            return equipeVendaD2dService.getEquipeVendas(usuario.getId());
        }
    }

    private String getEstrutura(Usuario usuario) {
        return usuario.isAgenteAutorizado() ? agenteAutorizadoService.getEstruturaByUsuarioIdAndAtivo(usuario.getId()) : null;
    }

    private String getTipoCanal(Usuario usuario) {
        var tipoCanal = usuario.getTipoCanal();
        return Objects.nonNull(tipoCanal) ? tipoCanal.name() : null;
    }

    private void setAdditionalInformation(OAuth2AccessToken token,
                                          Usuario usuario,
                                          User user,
                                          List<Integer> agentesAutorizados,
                                          List<Empresa> empresas,
                                          List<Integer> equipesSupervisionadas,
                                          List<Integer> equipesTecnicasSupervisionadas,
                                          List<EquipeVendaDto> equipeVendas,
                                          List<SelectResponse> sites) {
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
        token.getAdditionalInformation().put("subCanais", getSubCanais(usuario));
        token.getAdditionalInformation().put("equipeVendas", equipeVendas);
        token.getAdditionalInformation().put("organizacao", getOrganizacaoEmpresa(usuario));
        token.getAdditionalInformation().put("tiposFeeder", getTiposFeeder(usuario));
        token.getAdditionalInformation().put("fotoDiretorio", usuario.getFotoDiretorio());
        token.getAdditionalInformation().put("fotoNomeOriginal", usuario.getFotoNomeOriginal());
        token.getAdditionalInformation().put("fotoContentType", usuario.getFotoContentType());
        token.getAdditionalInformation().put("loginNetSales", usuario.getLoginNetSales());
        token.getAdditionalInformation().put("nomeEquipeVendaNetSales", usuario.getNomeEquipeVendaNetSales());
        token.getAdditionalInformation().put("codigoEquipeVendaNetSales", usuario.getCodigoEquipeVendaNetSales());
        token.getAdditionalInformation().put("canalNetSales", usuario.getCanalNetSales());
        token.getAdditionalInformation().put("organizacaoId", getOrganizacaoEmpresaId(usuario));
        token.getAdditionalInformation().put("organizacaoNome", getOrganizacaoEmpresaNome(usuario));
        token.getAdditionalInformation().put("organizacaoCodigo", getOrganizacaoEmpresaCodigo(usuario));

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
        token.getAdditionalInformation().put("equipesTecnicasSupervisionadas", equipesTecnicasSupervisionadas);
        token.getAdditionalInformation().put("estruturaAa", getEstrutura(usuario));
        token.getAdditionalInformation().put("tipoCanal", getTipoCanal(usuario));
        token.getAdditionalInformation().put("sites", sites);
        token.getAdditionalInformation().put("siteId", sites.stream()
                .map(SelectResponse::getValue)
                .findFirst()
                .orElse(null));
    }

    private String getOrganizacaoEmpresa(Usuario usuario) {
        return usuario.getOrganizacaoEmpresa() != null ? usuario.getOrganizacaoEmpresa().getDescricao() : "";
    }

    private String getOrganizacaoEmpresaCodigo(Usuario usuario) {
        return usuario.getOrganizacaoEmpresa() != null ? usuario.getOrganizacaoEmpresa().getCodigo() : "";
    }

    private String getOrganizacaoEmpresaNome(Usuario usuario) {
        return usuario.getOrganizacaoEmpresa() != null ? usuario.getOrganizacaoEmpresa().getNome() : "";
    }

    public static Set<String> getTiposFeeder(Usuario usuario) {
        if (CodigoNivel.MSO == usuario.getNivelCodigo()) {
            return ObjectUtils.isEmpty(usuario.getTiposFeeder()) ? Sets.newHashSet() : usuario.getTipoFeedersString();
        }
        return Sets.newHashSet();
    }

    private Integer getOrganizacaoEmpresaId(Usuario usuario) {
        return usuario.getOrganizacaoEmpresa() != null ? usuario.getOrganizacaoEmpresa().getId() : null;
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

    public static Set<String> getCanais(Usuario usuario) {
        switch (usuario.getNivelCodigo()) {
            case XBRAIN:
            case MSO:
                return Sets.newHashSet(ECanal.AGENTE_AUTORIZADO.name(), D2D_PROPRIO.name(), ATIVO_PROPRIO.name(),
                    ECanal.INTERNET.name());
            case OPERACAO:
                return ObjectUtils.isEmpty(usuario.getCanais()) ? Sets.newHashSet() : usuario.getCanaisString();
            default:
                return Sets.newHashSet(ECanal.AGENTE_AUTORIZADO.name());
        }
    }

    public Set<SubCanalDto> getSubCanais(Usuario usuario) {
        switch (usuario.getNivelCodigo()) {
            case XBRAIN:
            case MSO:
                return Sets.newHashSet(subCanalService.getAll());
            case OPERACAO:
                return ObjectUtils.isEmpty(usuario.getSubCanais())
                    ? Sets.newHashSet()
                    : SubCanalDto.of(usuario.getSubCanais());
            default:
                return Sets.newHashSet();
        }
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }

}
