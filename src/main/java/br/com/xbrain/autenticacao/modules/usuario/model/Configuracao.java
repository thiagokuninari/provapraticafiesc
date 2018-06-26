package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConfiguracaoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString(of = "usuario")
@EqualsAndHashCode(of = "usuario")
@Entity
@Table(name = "CONFIGURACAO")
public class Configuracao implements Serializable {

    @Id
    @SequenceGenerator(name = "SEQ_CONFIGURACAO", sequenceName = "SEQ_CONFIGURACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIGURACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID", unique = true)
    private Usuario usuario;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_USUARIO_CONFIGURACAO_CAD"),
            referencedColumnName = "ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @NotAudited
    @Column(name = "DATA_CADASTRO", updatable = false)
    private LocalDateTime cadastro;

    @Column(name = "RAMAL")
    private Integer ramal;

    public Configuracao() {
    }

    public Configuracao(Usuario usuario, Usuario usuarioCadastro, LocalDateTime cadastro, Integer ramal) {
        this.usuario = usuario;
        this.usuarioCadastro = usuarioCadastro;
        this.cadastro = cadastro;
        this.ramal = ramal;
    }

    public void configurar(UsuarioConfiguracaoDto dto) {
        this.usuario = new Usuario(dto.getUsuario());
        this.usuarioCadastro = new Usuario(dto.getUsuario());
        this.cadastro = LocalDateTime.now();
        this.ramal = dto.getRamal();
    }

    public void removerRamal() {
        this.ramal = null;
    }
}
