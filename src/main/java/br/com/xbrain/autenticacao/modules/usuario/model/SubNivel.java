package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUB_NIVEL")
public class SubNivel {

    @Id
    @GeneratedValue(generator = "SEQ_SUB_NIVEL", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_SUB_NIVEL", sequenceName = "SEQ_SUB_NIVEL", allocationSize = 1)
    private Integer id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "SITUACAO")
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_NIVEL", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_NIVEL"))
    private Nivel nivel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subNivel")
    private Set<CargoFuncionalidadeSubNivel> cargoFuncionalidadeSubNiveis;

    public List<Integer> getFuncionalidadesIds() {
        return this.cargoFuncionalidadeSubNiveis.stream()
            .map(CargoFuncionalidadeSubNivel::getFuncionalidade)
            .map(Funcionalidade::getId)
            .collect(Collectors.toList());
    }

    public List<Integer> getFuncionalidadesIdsByCargoId(Integer cargoId) {
        return this.cargoFuncionalidadeSubNiveis.stream()
            .filter(cargoFuncionalidadeSubNivel -> cargoFuncionalidadeSubNivel.getCargo() == null
                || cargoFuncionalidadeSubNivel.getCargo().getId().equals(cargoId))
            .map(CargoFuncionalidadeSubNivel::getFuncionalidade)
            .map(Funcionalidade::getId)
            .collect(Collectors.toList());
    }
}
