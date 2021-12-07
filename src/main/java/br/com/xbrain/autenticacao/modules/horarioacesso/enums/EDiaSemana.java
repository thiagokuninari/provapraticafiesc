package br.com.xbrain.autenticacao.modules.horarioacesso.enums;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EDiaSemana {
    
    DOMINGO(1, "DOMINGO", "Domingo", DayOfWeek.SUNDAY),
    SEGUNDA(2, "SEGUNDA", "Segunda-Feira", DayOfWeek.MONDAY),
    TERCA(3, "TERÇA", "Terça-Feira", DayOfWeek.TUESDAY),
    QUARTA(4, "QUARTA", "Quarta-Feira", DayOfWeek.WEDNESDAY),
    QUINTA(5, "QUINTA", "Quinta-Feira", DayOfWeek.THURSDAY),
    SEXTA(6, "SEXTA", "Sexta-Feira", DayOfWeek.FRIDAY),
    SABADO(7, "SÁBADO", "Sábado", DayOfWeek.SATURDAY);

    private Integer codigo;
    private String descricao;
    private String nomeCompleto;
    private DayOfWeek dayOfWeek;

    public static EDiaSemana valueOf(LocalDateTime horarioAtual) {
        return Stream.of(values())
            .filter(diaSemana -> Objects.equals(diaSemana.dayOfWeek, horarioAtual.getDayOfWeek()))
            .findFirst()
            .orElse(null);
    }
}
