package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "FERIADO")
public class Feriado {

    @Id
    @SequenceGenerator(name = "SEQ_FERIADO", sequenceName = "SEQ_FERIADO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FERIADO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_FERIADO", updatable = false, nullable = false)
    private LocalDate dataFeriado;

    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "FERIADO_NACIONAL", nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean feriadoNacional;

    @JoinColumn(name = "FK_CIDADE", referencedColumnName = "ID", updatable = false,
            foreignKey = @ForeignKey(name = "FK_FERIADO_CIDADE"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Cidade cidade;
}
