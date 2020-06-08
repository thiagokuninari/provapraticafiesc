package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "FERIADO_HISTORICO")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_FERIADO_HISTORICO", sequenceName = "SEQ_FERIADO_HISTORICO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FERIADO_HISTORICO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;

    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_FERIADO_HIST_USUARIO"),
        referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JoinColumn(name = "FK_FERIADO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_FERIADO_HISTORICO_FERIADO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Feriado feriado;

    @Column(name = "OBSERVACAO")
    private String observacao;

    public static FeriadoHistorico of(Feriado feriado, String observacao, Integer usuarioId) {
        var historico = new FeriadoHistorico();
        historico.setDataCadastro(LocalDateTime.now());
        historico.setUsuario(new Usuario(usuarioId));
        historico.setFeriado(feriado);
        historico.setObservacao(observacao);
        return historico;
    }

}
