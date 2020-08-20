package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginLogoutResponseTest {

    @Test
    public void of_responsesComDadosCorretos_quandoHouverTodosOsLoginsELogouts() {
        var acessos = List.of(
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:30:06", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T13:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T17:45:57", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:10:30", Eboolean.V)
        );

        assertThat(LoginLogoutResponse.of(acessos))
            .extracting("colaborador", "login", "logout")
            .containsExactlyInAnyOrder(
                umaResponseTuple("Maria Letícia", "11:30:06", "13:00:00"),
                umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30")
            );
    }

    @Test
    public void of_responsesComDadosCorretos_quandoHoverTodosOsLoginsELogoutsEDesordenados() {
        var acessos = List.of(
            umUsuarioAcesso("Maria Letícia", "2020-06-24T13:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:10:30", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T17:45:57", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:30:06", Eboolean.F)
        );

        assertThat(LoginLogoutResponse.of(acessos))
            .extracting("colaborador", "login", "logout")
            .containsExactlyInAnyOrder(
                umaResponseTuple("Maria Letícia", "11:30:06", "13:00:00"),
                umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30")
            );

        IntStream.rangeClosed(1, 200).forEach(seed -> {
            var acessosEmbaralhados = ListUtil.toShuffledList(acessos, new Random(seed));
            assertThat(LoginLogoutResponse.of(acessosEmbaralhados))
                .extracting("colaborador", "login", "logout")
                .containsExactlyInAnyOrder(
                    umaResponseTuple("Maria Letícia", "11:30:06", "13:00:00"),
                    umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30")
                );
        });
    }

    @Test
    public void of_responsesComDadosCorretos_quandoNaoHouverAlgunsLoginEouLogouts() {
        var acessos = List.of(
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:30:06", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:45:06", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T13:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T14:00:00", null),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T15:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T17:45:57", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T18:45:57", null),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:10:30", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:45:30", Eboolean.V)
        );

        assertThat(LoginLogoutResponse.of(acessos))
            .extracting("colaborador", "login", "logout")
            .containsExactlyInAnyOrder(
                umaResponseTuple("Maria Letícia", "11:30:06", null),
                umaResponseTuple("Maria Letícia", "11:45:06", "13:00:00"),
                umaResponseTuple("Maria Letícia", null, "15:00:00"),
                umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30"),
                umaResponseTuple("Maria Letícia", null, "21:45:30")
            );

        IntStream.rangeClosed(1, 200).forEach(seed -> {
            var acessosEmbaralhados = ListUtil.toShuffledList(acessos, new Random(seed));
            assertThat(LoginLogoutResponse.of(acessosEmbaralhados))
                .extracting("colaborador", "login", "logout")
                .containsExactlyInAnyOrder(
                    umaResponseTuple("Maria Letícia", "11:30:06", null),
                    umaResponseTuple("Maria Letícia", "11:45:06", "13:00:00"),
                    umaResponseTuple("Maria Letícia", null, "15:00:00"),
                    umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30"),
                    umaResponseTuple("Maria Letícia", null, "21:45:30")
                );
        });
    }

    private UsuarioAcesso umUsuarioAcesso(String usuarioNome, String dataCadastro, Eboolean flagLogout) {
        return UsuarioAcesso.builder()
            .usuario(Usuario.builder()
                .nome(usuarioNome)
                .build())
            .dataCadastro(LocalDateTime.parse(dataCadastro))
            .flagLogout(getFlagLogout(flagLogout))
            .build();
    }

    private Tuple umaResponseTuple(String colaborador, String login, String logout) {
        return Assertions.tuple(colaborador, getLocalTime(login), getLocalTime(logout));
    }

    private String getFlagLogout(Eboolean flagLogout) {
        return Optional.ofNullable(flagLogout)
            .map(Eboolean::name)
            .orElse(null);
    }

    private LocalTime getLocalTime(String localTime) {
        return Optional.ofNullable(localTime)
            .map(LocalTime::parse)
            .orElse(null);
    }
}
