package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MODALIDADE_EMPRESA")
@Builder
public class ModalidadeEmpresa {

    @Id
    @SequenceGenerator(name = "SEQ_MODALIDADE_EMPRESA", sequenceName = "SEQ_MODALIDADE_EMPRESA", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_MODALIDADE_EMPRESA", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODALIDADE_EMPRESA")
    private EModalidadeEmpresa modalidadeEmpresa;

    public ModalidadeEmpresa(Integer id) {
        this.id = id;
    }
}
