package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.MSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;

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
    private String cpf;
    private ESituacao situacao;
    private List<String> empresasNome;
    private List<Empresa> empresas;
    private Collection<? extends GrantedAuthority> permissoes;
    private String nivelCodigo;
    private CodigoDepartamento departamentoCodigo;
    private CodigoCargo cargoCodigo;
    private Integer organizacaoId;
    private String organizacaoCodigo;
    private Set<ECanal> canais;

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
        this.situacao = usuario.getSituacao();
        this.empresasNome = usuario.getEmpresasNome();
        this.nivelCodigo = usuario.getNivelCodigo().toString();
        this.departamentoCodigo = usuario.getDepartamentoCodigo();
        this.cargoCodigo = usuario.getCargoCodigo();
        this.canais = usuario.getCanais();
        getOrganizacao(usuario);
    }

    public UsuarioAutenticado(Usuario usuario, Collection<? extends GrantedAuthority> permissoes) {
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
        this.permissoes = permissoes;
        this.empresasNome = usuario.getEmpresasNome();
        this.nivelCodigo = usuario.getNivelCodigo().toString();
        this.departamentoCodigo = usuario.getDepartamentoCodigo();
        this.cargoCodigo = usuario.getCargoCodigo();
        getOrganizacao(usuario);
    }

    private void getOrganizacao(Usuario usuario) {
        Optional.ofNullable(usuario.getOrganizacao())
            .ifPresent(organizacao -> {
                this.organizacaoId = organizacao.getId();
                this.organizacaoCodigo = organizacao.getCodigo();
            });
    }

    public boolean hasPermissao(CodigoFuncionalidade codigoFuncionalidade) {
        return permissoes != null
            && permissoes
            .stream()
            .anyMatch(p -> p.getAuthority().equals("ROLE_" + codigoFuncionalidade));
    }

    public boolean hasCanal(ECanal canal) {
        return Objects.nonNull(this.canais) && this.canais.stream().anyMatch(c -> Objects.equals(c, canal));
    }

    public boolean isXbrain() {
        return XBRAIN == getNivelCodigoEnum();
    }

    public boolean isMso() {
        return MSO == usuario.getNivelCodigo();
    }

    public void hasPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId,
                                                   List<Integer> agentesAutorizadosIdDoUsuario) {
        if (isAgenteAutorizado()
            && (ObjectUtils.isEmpty(agentesAutorizadosIdDoUsuario)
            || !agentesAutorizadosIdDoUsuario.contains(agenteAutorizadoId))) {
            throw new PermissaoException();
        }
    }

    private boolean isAgenteAutorizado() {
        return !ObjectUtils.isEmpty(nivelCodigo) && CodigoNivel.valueOf(nivelCodigo) == CodigoNivel.AGENTE_AUTORIZADO;
    }

    public CodigoNivel getNivelCodigoEnum() {
        return CodigoNivel.valueOf(nivelCodigo);
    }

    public boolean isUsuarioEquipeVendas() {
        return !ObjectUtils.isEmpty(usuario) && usuario.isUsuarioEquipeVendas();
    }

    public boolean isCoordenadorOperacao() {
        return cargoCodigo.equals(CodigoCargo.COORDENADOR_OPERACAO);
    }

    public boolean isGerenteOperacao() {
        return cargoCodigo.equals(CodigoCargo.GERENTE_OPERACAO);
    }

    public boolean isBackoffice() {
        return !ObjectUtils.isEmpty(nivelCodigo) && CodigoNivel.valueOf(nivelCodigo).equals(CodigoNivel.BACKOFFICE);
    }
}
