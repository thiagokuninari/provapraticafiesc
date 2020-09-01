package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class LoginLogoutCsvTest {

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
            + "SELENA GOMEZ;20/11/2019;2;11:20:00;;12:05:21;17:00:59;;;04:55:38\n"
            + "MARAÍSA SILVA;06/12/2018;3;13:00:00;14:30:00;;18:45:00;;21:00:00;01:30:00\n"
            + "THIAGO MOREIRA;29/02/2020;1;13:00:00;14:50:00;17:40:51;;;;01:50:00";
        assertThat(LoginLogoutCsv.getCsv(csvs)).isEqualTo(expected);
    }

    @Test
    public void of_csvsComInformacoesCorretas_quandoDiferentesColaboradores() {
        var acessos = List.of(
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:30:06", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T13:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T17:45:57", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:10:30", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T12:15:40", Eboolean.F),
            umUsuarioAcesso("João Pedro", "2020-06-24T12:50:31", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T13:00:40", null),
            umUsuarioAcesso("João Pedro", "2020-06-24T23:00:00", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T14:15:40", null),
            umUsuarioAcesso("João Pedro", "2020-07-01T12:30:41", Eboolean.F),
            umUsuarioAcesso("João Pedro", "2020-07-01T12:55:39", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-07-01T13:00:40", null),
            umUsuarioAcesso("João Pedro", "2020-07-01T23:00:00", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-07-01T14:15:40", null)
        );

        var acessosLists = Stream.<List<UsuarioAcesso>>builder();
        acessosLists.add(acessos);
        IntStream.rangeClosed(1, 200)
            .mapToObj(seed -> ListUtil.toShuffledList(acessos, new Random(seed)))
            .forEach(acessosLists::add);

        acessosLists.build().forEach(acessosATestar -> {
            var csvs = LoginLogoutCsv.of(acessosATestar);

            assertThat(csvs).extracting("colaborador", "data")
                .containsExactlyInAnyOrder(
                    tuple("Maria Letícia", "24/06/2020"),
                    tuple("João Pedro", "01/07/2020"),
                    tuple("João Pedro", "24/06/2020"));

            var csvATestar = getCsvPorColaboradorEData(csvs, "Maria Letícia", "24/06/2020");
            assertThat(csvATestar.getLogins())
                .containsExactly(
                    LocalTime.of(11, 30, 6),
                    LocalTime.of(17, 45, 57));
            assertThat(csvATestar.getLogouts())
                .containsExactly(
                    LocalTime.of(13, 0, 0),
                    LocalTime.of(21, 10, 30));

            csvATestar = getCsvPorColaboradorEData(csvs, "João Pedro", "24/06/2020");
            assertThat(csvATestar.getLogins())
                .containsExactly(
                    LocalTime.of(12, 15, 40),
                    null);
            assertThat(csvATestar.getLogouts())
                .containsExactly(
                    LocalTime.of(12, 50, 31),
                    LocalTime.of(23, 0, 0));

            csvATestar = getCsvPorColaboradorEData(csvs, "João Pedro", "01/07/2020");
            assertThat(csvATestar.getLogins())
                .containsExactly(
                    LocalTime.of(12, 30, 41),
                    null);
            assertThat(csvATestar.getLogouts())
                .containsExactly(
                    LocalTime.of(12, 55, 39),
                    LocalTime.of(23, 0, 0));
        });
    }

    private static final Map<String, Integer> USUARIO_NOME_ID = ImmutableMap.<String, Integer>builder()
        .put("Maria Letícia", 1)
        .put("João Pedro", 2)
        .build();

    private UsuarioAcesso umUsuarioAcesso(String usuarioNome, String dataCadastro, Eboolean flagLogout) {
        return UsuarioAcesso.builder()
            .usuario(Usuario.builder()
                .id(USUARIO_NOME_ID.get(usuarioNome))
                .nome(usuarioNome)
                .build())
            .dataCadastro(LocalDateTime.parse(dataCadastro))
            .flagLogout(getFlagLogout(flagLogout))
            .build();
    }

    private String getFlagLogout(Eboolean flagLogout) {
        return Optional.ofNullable(flagLogout)
            .map(Eboolean::name)
            .orElse(null);
    }

    private LoginLogoutCsv getCsvPorColaboradorEData(Collection<LoginLogoutCsv> csvs, String colaborador, String data) {
        return csvs.stream()
            .filter(csv -> new EqualsBuilder()
                .append(csv.getColaborador(), colaborador)
                .append(csv.getData(), data)
                .isEquals())
            .findFirst()
            .orElseThrow();
    }
}
