package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "FUNCIONALIDADE")
@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funcionalidade {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_FUNCIONALIDADE", sequenceName = "SEQ_FUNCIONALIDADE", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FUNCIONALIDADE", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME")
    private String nome;

    @NotNull
    @Column(name = "ROLE")
    private String role;

    @NotNull
    @JoinColumn(name = "FK_APLICACAO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FUNCIONALIDADE_APLICACAO"), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Aplicacao aplicacao;

    @Column(name = "PERMISSAO_TELA", length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean permissaoTela;

    @OneToMany(mappedBy = "funcionalidade", fetch = FetchType.LAZY)
    private List<FuncionalidadeCanal> canais;

    @Transient
    private boolean especial;
}
