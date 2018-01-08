package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@JsonIgnoreProperties
public class UsuarioAutenticado {

    private int id;
    private String nome;
    private String email;
    private String cargo;
    private String departamento;
    private String nivel;
    private String cpf;
    private ESituacao situacao;
    private Collection<? extends GrantedAuthority> permissoes;

    public UsuarioAutenticado(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo().getCodigo();
        this.departamento = usuario.getDepartamento().getCodigo();
        this.nivel = usuario.getCargo().getNivel().getCodigo();
        this.cpf = usuario.getCpf();
        this.situacao = usuario.getSituacao();
    }

    public UsuarioAutenticado(Usuario usuario, Collection<? extends GrantedAuthority> permissoes) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo().getCodigo();
        this.departamento = usuario.getDepartamento().getCodigo();
        this.nivel = usuario.getCargo().getNivel().getCodigo();
        this.cpf = usuario.getCpf();
        this.situacao = usuario.getSituacao();
        this.permissoes = permissoes;
    }
}
