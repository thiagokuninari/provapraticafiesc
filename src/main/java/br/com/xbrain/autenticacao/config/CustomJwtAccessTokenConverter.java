package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        log.info("Iniciando o processo de adição de informações ao token");
        defaultOAuth2AccessToken.setAdditionalInformation(new LinkedHashMap<>(accessToken.getAdditionalInformation()));
        if (authentication.getUserAuthentication() != null) {
            User userAuth = (User) authentication.getUserAuthentication().getPrincipal();
            log.info("userAuth {}", userAuth);
            usuarioRepository
                .findComplete(Integer.valueOf(userAuth.getUsername().split(Pattern.quote("-"))[0]))
                .ifPresent(usuario -> setAdditionalInformation(
                    defaultOAuth2AccessToken,
                    usuario,
                    userAuth,
                    getAgentesAutorizadosPermitidos(usuario),
                    getEmpresasDoUsuario(usuario),
                    getEquipesSupervisionadas(usuario),
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
        log.info("Is agente autorizado? {}", usuario.isAgenteAutorizado());
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
                                          List<EquipeVendaDto> equipeVendas,
                                          List<SelectResponse> sites) {
        log.info("Adicionando usuarioId ao token");
        token.getAdditionalInformation().put("usuarioId", usuario.getId());
        log.info("Adicionando cpf ao token");
        token.getAdditionalInformation().put("cpf", usuario.getCpf());
        log.info("Adicionando email ao token");
        token.getAdditionalInformation().put("email", usuario.getEmail());
        log.info("Adicionando login ao token");
        token.getAdditionalInformation().put("login", user.getUsername());
        log.info("Adicionando nome ao token");
        token.getAdditionalInformation().put("nome", usuario.getNome());
        log.info("Adicionando nomeAbreviado ao token");
        token.getAdditionalInformation().put("nomeAbreviado", StringUtil.getNomeAbreviado(usuario.getNome()));
        log.info("Adicionando alterarSenha ao token");
        token.getAdditionalInformation().put("alterarSenha", usuario.getAlterarSenha());
        log.info("Adicionando nivel ao token");
        token.getAdditionalInformation().put("nivel", usuario.getCargo().getNivel().getNome());
        log.info("Adicionando nivelCodigo ao token");
        token.getAdditionalInformation().put("nivelCodigo", usuario.getCargo().getNivel().getCodigo());
        log.info("Adicionando departamento ao token");
        token.getAdditionalInformation().put("departamento", usuario.getDepartamento().getNome());
        log.info("Adicionando departamentoCodigo ao token");
        token.getAdditionalInformation().put("departamentoCodigo", usuario.getDepartamento().getCodigo());
        log.info("Adicionando cargo ao token");
        token.getAdditionalInformation().put("cargo", usuario.getCargo().getNome());
        log.info("Adicionando cargoCodigo ao token");
        token.getAdditionalInformation().put("cargoCodigo", usuario.getCargo().getCodigo());
        log.info("Adicionando cargoId ao token");
        token.getAdditionalInformation().put("cargoId", usuario.getCargoId());
        log.info("Adicionando nivelId ao token");
        token.getAdditionalInformation().put("nivelId", usuario.getNivelId());
        log.info("Adicionando departamentoId ao token");
        token.getAdditionalInformation().put("departamentoId", usuario.getDepartamentoId());
        log.info("Adicionando canais ao token");
        token.getAdditionalInformation().put("canais", getCanais(usuario));
        log.info("Adicionando subCanais ao token");
        token.getAdditionalInformation().put("subCanais", getSubCanais(usuario));
        log.info("Adicionando equipeVendas ao token");
        token.getAdditionalInformation().put("equipeVendas", equipeVendas);
        log.info("Adicionando organizacao ao token");
        token.getAdditionalInformation().put("organizacao", getOrganizacaoEmpresa(usuario));
        log.info("Adicionando tiposFeeder ao token");
        token.getAdditionalInformation().put("tiposFeeder", getTiposFeeder(usuario));
        log.info("Adicionando fotoDiretorio ao token");
        token.getAdditionalInformation().put("fotoDiretorio", usuario.getFotoDiretorio());
        log.info("Adicionando fotoNomeOriginal ao token");
        token.getAdditionalInformation().put("fotoNomeOriginal", usuario.getFotoNomeOriginal());
        log.info("Adicionando fotoContentType ao token");
        token.getAdditionalInformation().put("fotoContentType", usuario.getFotoContentType());
        log.info("Adicionando loginNetSales ao token");
        token.getAdditionalInformation().put("loginNetSales", usuario.getLoginNetSales());
        log.info("Adicionando nomeEquipeVendaNetSales ao token");
        token.getAdditionalInformation().put("nomeEquipeVendaNetSales", usuario.getNomeEquipeVendaNetSales());
        log.info("Adicionando codigoEquipeVendaNetSales ao token");
        token.getAdditionalInformation().put("codigoEquipeVendaNetSales", usuario.getCodigoEquipeVendaNetSales());
        log.info("Adicionando canalNetSales ao token");
        token.getAdditionalInformation().put("canalNetSales", usuario.getCanalNetSales());
        log.info("Adicionando organizacaoId ao token");
        token.getAdditionalInformation().put("organizacaoId", getOrganizacaoEmpresaId(usuario));
        log.info("Adicionando organizacaoNome ao token");
        token.getAdditionalInformation().put("organizacaoNome", getOrganizacaoEmpresaNome(usuario));
        log.info("Adicionando organizacaoCodigo ao token");
        token.getAdditionalInformation().put("organizacaoCodigo", getOrganizacaoEmpresaCodigo(usuario));

        if (!isEmpty(empresas)) {
            log.info("Adicionando empresas ao token");
            token.getAdditionalInformation()
                .put("empresas", getListaEmpresaPorCampo(empresas, Empresa::getId));

            log.info("Adicionando empresasNome ao token");
            token.getAdditionalInformation()
                .put("empresasNome", getListaEmpresaPorCampo(empresas, Empresa::getNome));

            log.info("Adicionando empresasCodigo ao token");
            token.getAdditionalInformation()
                .put("empresasCodigo", getListaEmpresaPorCampo(empresas, Empresa::getCodigo));
        }

        log.info("Adicionando unidadesNegocios ao token");
        token.getAdditionalInformation().put("unidadesNegocios", usuario.getUnidadesNegociosId());
        log.info("Adicionando agentesAutorizados ao token");
        token.getAdditionalInformation().put("agentesAutorizados", agentesAutorizados);
        log.info("Adicionando active ao token");
        token.getAdditionalInformation().put("active", true);
        log.info("Adicionando authorities ao token");
        token.getAdditionalInformation().put("authorities",
            user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray());
        log.info("Adicionando aplicacoes ao token");
        token.getAdditionalInformation().put("aplicacoes",
            getAplicacoes(usuario));
        log.info("Adicionando equipesSupervisionadas ao token");
        token.getAdditionalInformation().put("equipesSupervisionadas",
            equipesSupervisionadas);
        log.info("Adicionando estruturaAa ao token");
        token.getAdditionalInformation().put("estruturaAa", getEstrutura(usuario));
        log.info("Adicionando tipoCanal ao token");
        token.getAdditionalInformation().put("tipoCanal", getTipoCanal(usuario));
        log.info("Adicionando sites ao token");
        token.getAdditionalInformation().put("sites", sites);
        log.info("Adicionando siteId ao token");
        token.getAdditionalInformation().put("siteId", sites.stream()
            .map(SelectResponse::getValue)
            .findFirst()
            .orElse(null));

        log.info("Informações adicionadas ao token");
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
        log.info("Canais do usuario {}", usuario.getCanaisString());

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
        log.info("Nível do usuario {}", usuario.getNivelCodigo());

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
