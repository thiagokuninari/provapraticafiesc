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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class LoginLogoutCsvTest {

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
