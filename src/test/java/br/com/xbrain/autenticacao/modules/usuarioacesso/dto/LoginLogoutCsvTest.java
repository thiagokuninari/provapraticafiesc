package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import org.junit.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginLogoutCsvTest {

    @Test
    public void getQuantidadeDeLogouts_quantidadeCertaDeLogouts_quandoRegistradosTodosOsLogouts() {
        var logouts = List.of(
            LocalTime.of(11, 0),
            LocalTime.of(18, 30),
            LocalTime.of(19, 10, 12)
        );
        var csv = LoginLogoutCsv.builder()
            .logouts(logouts)
            .build();

        assertThat(csv.getQuantidadeDeLogouts()).isEqualTo(3);
    }

    @Test
    public void getQuantidadeDeLogouts_quantidadeDeLogoutsRegistrados_quandoUltimoLogoutNaoRegistrado() {
        var logouts = Stream.of(
            LocalTime.of(18, 30),
            null
        ).collect(Collectors.toList());
        var csv = LoginLogoutCsv.builder()
            .logouts(logouts)
            .build();

        assertThat(csv.getQuantidadeDeLogouts()).isEqualTo(1);
    }

    @Test
    public void getTempoTotalLogado_duracaoDoTempoTotalLogado() {
        var logins = Stream.of(
            LocalTime.of(11, 0),
            null,
            LocalTime.of(17, 0),
            LocalTime.of(18, 30),
            null
        );
        var logouts = Stream.of(
            LocalTime.of(14, 47, 36),
            LocalTime.of(16, 0),
            null,
            LocalTime.of(19, 10, 12),
            null
        );
        var csv = LoginLogoutCsv.builder()
            .logins(logins.collect(Collectors.toList()))
            .logouts(logouts.collect(Collectors.toList()))
            .build();

        assertThat(csv.getTempoTotalLogado()).isEqualTo("04:27:48");
    }

    @Test
    public void getCsv_textoCsvDosDtosCsv() {
        var csvs = List.of(
            LoginLogoutCsv.builder()
                .colaborador("Selena Gomez")
                .data("20/11/2019")
                .logins(List.of(LocalTime.of(11, 20), LocalTime.of(12, 5, 21)))
                .logouts(Stream.of(null, LocalTime.of(17, 0, 59)).collect(Collectors.toList()))
                .build(),
            LoginLogoutCsv.builder()
                .colaborador("Maraísa Silva")
                .data("06/12/2018")
                .logins(List.of(LocalTime.of(13, 0)))
                .logouts(List.of(LocalTime.of(14, 30), LocalTime.of(18, 45), LocalTime.of(21, 0)))
                .build(),
            LoginLogoutCsv.builder()
                .colaborador("Thiago Moreira")
                .data("29/02/2020")
                .logins(List.of(LocalTime.of(13, 0), LocalTime.of(17, 40, 51)))
                .logouts(List.of(LocalTime.of(14, 50)))
                .build()
        );

        var expected = "COLABORADOR;DATA;QUANTIDADE DE LOGOUT;"
            + "HORÁRIO LOGIN 1;HORÁRIO LOGOUT 1;HORÁRIO LOGIN 2;HORÁRIO LOGOUT 2;HORÁRIO LOGIN 3;HORÁRIO LOGOUT 3;"
            + "TEMPO TOTAL LOGADO\n"
            + "SELENA GOMEZ;20/11/2019;1;11:20:00;;12:05:21;17:00:59;;;04:55:38\n"
            + "MARAÍSA SILVA;06/12/2018;3;13:00:00;14:30:00;;18:45:00;;21:00:00;01:30:00\n"
            + "THIAGO MOREIRA;29/02/2020;1;13:00:00;14:50:00;17:40:51;;;;01:50:00";
        assertThat(LoginLogoutCsv.getCsv(csvs)).isEqualTo(expected);
    }
}
