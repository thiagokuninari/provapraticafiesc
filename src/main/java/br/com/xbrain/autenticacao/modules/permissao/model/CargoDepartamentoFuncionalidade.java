package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARGO_DEPART_FUNC")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@NoArgsConstructor
public class CargoDepartamentoFuncionalidade {

    @Id
    @SequenceGenerator(name = "SEQ_CARGO_DEPART_FUNC", sequenceName = "SEQ_CARGO_DEPART_FUNC", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CARGO_DEPART_FUNC", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JsonIgnore
    @JoinColumn(name = "FK_CARGO", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_CARGO"),
            referencedColumnName = "id", updatable = false, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cargo cargo;

    @JoinColumn(name = "FK_DEPARTAMENTO", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_DEP"),
            referencedColumnName = "id", updatable = false, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Departamento departamento;

    @JoinColumn(name = "FK_FUNCIONALIDADE", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_FUNC"),
            referencedColumnName = "id", updatable = false, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Funcionalidade funcionalidade;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_USU"),
            referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Column(name = "DATA_CADASTRO", updatable = false)
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL")
    private ECanal canal;

    @Builder
    public CargoDepartamentoFuncionalidade(Integer id, Cargo cargo, Departamento departamento,
                                           Funcionalidade funcionalidade, Usuario usuario,
                                           LocalDateTime dataCadastro) {
        this.id = id;
        this.cargo = cargo;
        this.departamento = departamento;
        this.funcionalidade = funcionalidade;
        this.usuario = usuario;
        this.dataCadastro = dataCadastro;
    }

    public Integer getCargoId() {
        return this.cargo != null ? this.cargo.getId() : null;
    }

    public Integer getDepartamentoId() {
        return this.departamento != null ? this.departamento.getId() : null;
    }
}
