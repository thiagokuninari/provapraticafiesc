package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ENivelEmpresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NIVEL_EMPRESA")
public class NivelEmpresa {

    @Id
    @SequenceGenerator(name = "SEQ_NIVEL_EMPRESA", sequenceName = "SEQ_NIVEL_EMPRESA", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_NIVEL_EMPRESA", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_EMPRESA")
    private ENivelEmpresa nivelEmpresa;
}
