package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.config.CustomJwtAccessTokenConverter;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.MSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;

@SuppressWarnings("PMD.TooManyStaticImports")
@EqualsAndHashCode(callSuper = false)
@Data
@JsonIgnoreProperties
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAutenticado extends OAuth2Request {

    private Usuario usuario;
    private int id;
    private String nome;
    private String email;
    private Integer cargoId;
    private String cargo;
    private Integer departamentoId;
    private String departamento;
    private String nivel;
    private Integer nivelId;
    private String loginNetSales;
    private String nomeEquipeVendaNetSales;
    private String codigoEquipeVendaNetSales;
    private String cpf;
    private ESituacao situacao;
    private List<String> empresasNome;
    private List<Empresa> empresas;
    private List<Integer> agentesAutorizados;
    private Collection<? extends GrantedAuthority> permissoes;
    private String nivelCodigo;
    private CodigoDepartamento departamentoCodigo;
    private CodigoCargo cargoCodigo;
    private Integer organizacaoId;
    private String organizacaoCodigo;
    private String organizacaoNome;
    private Set<ECanal> canais;
    private Set<SubCanalDto> subCanais;
    private Integer siteId;

    public UsuarioAutenticado(OAuth2Request other) {
        super(other);
    }

    public UsuarioAutenticado(Usuario usuario) {
        this.usuario = usuario;
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargoId = usuario.getCargoId();
        this.departamentoId = usuario.getDepartamentoId();
        this.cargo = usuario.getCargoCodigo().toString();
        this.departamento = usuario.getDepartamentoCodigo().toString();
        this.nivel = usuario.getNivelCodigo().toString();
        this.nivelId = usuario.getNivelId();
        this.cpf = usuario.getCpf();
        this.loginNetSales = usuario.getLoginNetSales();
        this.nomeEquipeVendaNetSales = usuario.getNomeEquipeVendaNetSales();
        this.codigoEquipeVendaNetSales = usuario.getCodigoEquipeVendaNetSales();
        this.situacao = usuario.getSituacao();
        this.empresasNome = usuario.getEmpresasNome();
        this.nivelCodigo = usuario.getNivelCodigo().toString();
        this.departamentoCodigo = usuario.getDepartamentoCodigo();
        this.cargoCodigo = usuario.getCargoCodigo();
        this.canais = usuario.getCanais();
        this.subCanais = usuario.getSubCanais().stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toSet());
        getOrganizacaoEmpresa(usuario);
    }

    public UsuarioAutenticado(Usuario usuario,
                              Collection<? extends GrantedAuthority> permissoes) {
        this.usuario = usuario;
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargoId = usuario.getCargoId();
        this.departamentoId = usuario.getDepartamentoId();
        this.cargo = usuario.getCargo().getNome();
        this.departamento = usuario.getDepartamento().getNome();
        this.nivel = usuario.getNivelCodigo().toString();
        this.nivelId = usuario.getNivelId();
        this.cpf = usuario.getCpf();
        this.situacao = usuario.getSituacao();
        this.loginNetSales = usuario.getLoginNetSales();
        this.nomeEquipeVendaNetSales = usuario.getNomeEquipeVendaNetSales();
        this.codigoEquipeVendaNetSales = usuario.getCodigoEquipeVendaNetSales();
        this.permissoes = permissoes;
        this.empresasNome = usuario.getEmpresasNome();
        this.nivelCodigo = usuario.getNivelCodigo().toString();
        this.departamentoCodigo = usuario.getDepartamentoCodigo();
        this.cargoCodigo = usuario.getCargoCodigo();
        this.canais = usuario.getCanais();
        this.subCanais = usuario.getSubCanais().stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toSet());
        getOrganizacaoEmpresa(usuario);
    }

    private void getOrganizacaoEmpresa(Usuario usuario) {
        Optional.ofNullable(usuario.getOrganizacaoEmpresa())
            .ifPresent(organizacao -> {
                this.organizacaoId = organizacao.getId();
                this.organizacaoNome = organizacao.getNome();
                this.organizacaoCodigo = organizacao.getCodigo();
            });
    }

    public boolean hasPermissao(CodigoFuncionalidade codigoFuncionalidade) {
        return permissoes != null
            && permissoes
            .stream()
            .anyMatch(p -> p.getAuthority().equals("ROLE_" + codigoFuncionalidade));
    }

    public boolean isOperacao() {
        return getNivelCodigoEnum() == CodigoNivel.OPERACAO;
    }

    public boolean hasCanal(ECanal canal) {
        return Objects.nonNull(this.canais) && this.canais.stream().anyMatch(c -> Objects.equals(c, canal));
    }

    public boolean hasSubCanal(SubCanal subCanal) {
        return Objects.nonNull(this.subCanais) && this.subCanais.stream().anyMatch(s -> Objects.equals(s, subCanal));
    }

    public boolean isXbrain() {
        return XBRAIN == getNivelCodigoEnum();
    }

    public boolean isXbrainOuMso() {
        return isXbrain() || isMso();
    }

    public boolean isMso() {
        return MSO == getNivelCodigoEnum();
    }

    public boolean isMsoOrXbrain() {
        return isMso() || isXbrain();
    }

    public void hasPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId,
                                                   List<Integer> agentesAutorizadosIdDoUsuario) {
        if (isAgenteAutorizado()
            && (ObjectUtils.isEmpty(agentesAutorizadosIdDoUsuario)
            || !agentesAutorizadosIdDoUsuario.contains(agenteAutorizadoId))) {
            throw new PermissaoException();
        }
    }

    public boolean isAgenteAutorizado() {
        return !ObjectUtils.isEmpty(nivelCodigo) && CodigoNivel.valueOf(nivelCodigo) == CodigoNivel.AGENTE_AUTORIZADO;
    }

    public CodigoNivel getNivelCodigoEnum() {
        return CodigoNivel.valueOf(nivelCodigo);
    }

    public boolean isUsuarioEquipeVendas() {
        return !ObjectUtils.isEmpty(usuario) && usuario.isUsuarioEquipeVendas();
    }

    public boolean isAssistenteOperacao() {
        return cargoCodigo == ASSISTENTE_OPERACAO && isOperacao();
    }

    public boolean isCoordenadorOperacao() {
        return cargoCodigo == COORDENADOR_OPERACAO;
    }

    public boolean isGerenteOperacao() {
        return cargoCodigo == GERENTE_OPERACAO;
    }

    public boolean isSupervisorOperacao() {
        return cargoCodigo == SUPERVISOR_OPERACAO;
    }

    public boolean isExecutivo() {
        return cargoCodigo == EXECUTIVO;
    }

    public boolean isExecutivoHunter() {
        return cargoCodigo == EXECUTIVO_HUNTER;
    }

    public boolean isExecutivoOuExecutivoHunter() {
        return isExecutivo() || isExecutivoHunter();
    }

    public boolean isBackoffice() {
        return !ObjectUtils.isEmpty(nivelCodigo) && CodigoNivel.valueOf(nivelCodigo).equals(CodigoNivel.BACKOFFICE);
    }

    public boolean isGerenteInternetOperacao() {
        return isOperacao() && INTERNET_GERENTE.equals(cargoCodigo);
    }

    public boolean isSupervisorInternetOperacao() {
        return isOperacao() && INTERNET_SUPERVISOR.equals(cargoCodigo);
    }

    public boolean isCoordenadorInternetOperacao() {
        return isOperacao() && INTERNET_COORDENADOR.equals(cargoCodigo);
    }

    public boolean isVendedorInternetOperacao() {
        return isOperacao() && INTERNET_VENDEDOR.equals(cargoCodigo);
    }

    public boolean isBackofficeInternetOperacao() {
        return isOperacao() && INTERNET_BACKOFFICE.equals(cargoCodigo);
    }

    public boolean haveCanalAgenteAutorizado() {
        return haveCanal(AGENTE_AUTORIZADO);
    }

    private boolean haveCanal(ECanal canal) {
        return ObjectUtils.isEmpty(usuario.getCanais())
            ? CustomJwtAccessTokenConverter.getCanais(usuario).contains(canal.name())
            : usuario.getCanais()
            .contains(canal);
    }

    public boolean haveCanalDoorToDoor() {
        return haveCanal(D2D_PROPRIO);
    }

    public boolean isOperadorTelevendasAtivoLocal() {
        return cargoCodigo.equals(OPERACAO_TELEVENDAS)
            && hasCanal(ECanal.ATIVO_PROPRIO);
    }

    public void validarAdministrador() {
        if (!isXbrain()) {
            throw new PermissaoException("Usuário não autorizado!");
        }
    }
}
