package com.provapratica.api.domain;

import com.provapratica.api.enums.ENivelUsuario;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NIVEL")
@EqualsAndHashCode(of = "id")
public class Nivel {

    @Id
    @SequenceGenerator(name = "SEQ_NIVEL", sequenceName = "SEQ_NIVEL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_NIVEL", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @Column(name = "CODIGO", length = 80)
    @Enumerated(EnumType.STRING)
    private ENivelUsuario codigo;

    public Nivel(Integer id) {
        this.id = id;
    }
}
