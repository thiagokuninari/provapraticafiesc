package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARGO_DEPART_FUNC")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class CargoDepartamentoFuncionalidade {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_CARGO_DEPART_FUNC",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_CARGO_DEPART_FUNC")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CARGO_DEPART_FUNC")
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

    @JoinColumn(name = "FK_EMPRESA", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_EMPR"),
            referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "FK_UNIDADE_NEGOCIO", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_UNID"),
            referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadeNegocio unidadeNegocio;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_CARGO_DEPART_FUNC_USU"),
            referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Column(name = "DATA_CADASTRO", updatable = false)
    private LocalDateTime dataCadastro;
}
