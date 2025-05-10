package com.provapratica.api.domain;

import com.provapratica.api.dto.EspecialidadeRequest;
import com.provapratica.api.enums.EStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ESPECIALIDADE")
@EqualsAndHashCode(of = "id")
public class Especialidade {

    @Id
    @SequenceGenerator(name = "SEQ_ESPECIALIDADE", sequenceName = "SEQ_ESPECIALIDADE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ESPECIALIDADE")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME_ESPECIALIDADE", nullable = false)
    private String nomeEspecialidade;

    @Column(name = "CODIGO_ESPECIALIDADE", nullable = false)
    private String codigoEspecialidade;

    @Column(name = "STATUS_ESPECIALIDADE", nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatus statusEspecialidade;

    public static Especialidade of(EspecialidadeRequest especialidadeRequest) {
        return Especialidade.builder()
                .nomeEspecialidade(especialidadeRequest.getNomeEspecialidade())
                .codigoEspecialidade(especialidadeRequest.getNomeEspecialidade())
                .statusEspecialidade(EStatus.ATIVO)
                .build();
    }
}
