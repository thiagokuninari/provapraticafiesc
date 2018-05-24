package br.com.xbrain.autenticacao.modules.canaisnet.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ativo {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME_FANTASIA", length = 120)
    private String nomeFantasia;

    @Column(name = "RAZAO_SOCIAL")
    private String razaoSocial;

    @Column(name = "CNPJ")
    private String cnpj;

    @JsonIgnore
    @JoinTable(name = "USUARIO_ATIVO", joinColumns = {
            @JoinColumn(name = "FK_ATIVO", foreignKey = @ForeignKey(name="FK_ATV_USR"),
                    referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name="FK_USR_ATV"),
                    referencedColumnName = "id")})
    @ManyToMany
    private List<Usuario> usuarios;
}
