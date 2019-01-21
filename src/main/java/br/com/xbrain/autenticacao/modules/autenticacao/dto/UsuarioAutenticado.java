package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Collection;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;

@Data
@JsonIgnoreProperties
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
    }

    public boolean hasPermissao(CodigoFuncionalidade codigoFuncionalidade) {
        return permissoes != null
                && permissoes
                        .stream()
                        .filter(p -> p.getAuthority().equals("ROLE_" + codigoFuncionalidade))
                        .count() > 0;
    }

    public boolean isXbrain() {
        return usuario.getNivelCodigo() == XBRAIN;
    }

    public void hasPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId, List<Integer> agentesAutorizadosIdDoUsuario) {
        if (isAgenteAutorizado()
                && (agentesAutorizadosIdDoUsuario == null || !agentesAutorizadosIdDoUsuario.contains(agenteAutorizadoId))) {
            throw new PermissaoException();
        }
    }

    public boolean isAgenteAutorizado() {
        return nivelCodigo != null && CodigoNivel.valueOf(nivelCodigo) == CodigoNivel.AGENTE_AUTORIZADO;
    }
}
