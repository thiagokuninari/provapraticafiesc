package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalCompletDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "SUB_CANAL")
@Data
@ToString(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCanal {

    @Id
    @SequenceGenerator(name = "SEQ_SUB_CANAL", sequenceName = "SEQ_SUBCANAL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SUB_CANAL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "CODIGO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ETipoCanal codigo;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "NOVA_CHECAGEM_CREDITO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemCredito;

    @Column(name = "NOVA_CHECAGEM_VIABILIDADE", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemViabilidade;

    @Column(name = "REALIZAR_ENRIQUECIMENTO_END", length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean realizarEnriquecimentoEnd;

    public SubCanal(Integer id) {
        this.id = id;
    }

    public SubCanal editar(SubCanalCompletDto request) {
        this.codigo = request.getCodigo();
        this.nome = request.getNome().trim();
        this.situacao = request.getSituacao();
        this.novaChecagemCredito = request.getNovaChecagemCredito();
        this.novaChecagemViabilidade = request.getNovaChecagemViabilidade();
        this.realizarEnriquecimentoEnd = request.getRealizarEnriquecimentoEnd();
        return this;
    }
}
