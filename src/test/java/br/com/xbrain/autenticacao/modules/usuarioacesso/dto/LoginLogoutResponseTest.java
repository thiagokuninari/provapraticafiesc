package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginLogoutResponseTest {

    @Test
    public void getTempoTotalLogado_tempoCorretoTotalLogado_quandoTiverHorarioDeLoginEDeLogout() {
        var response = umaLoginLogoutResponse("17:25:30", "20:20:42");
        assertThat(response.getTempoTotalLogado()).isEqualTo("02:55:12");
    }

    @Test
    public void getTempoTotalLogado_null_quandoHorarioDeLoginVierDepoisDoDeLogout() {
        var response = umaLoginLogoutResponse("20:20:42", "17:25:30");
        assertThat(response.getTempoTotalLogado()).isNull();
    }

    @Test
    public void getTempoTotalLogado_null_quandoNaoHouverHorarioDeLogout() {
        var response = umaLoginLogoutResponse("17:25:30", null);
        assertThat(response.getTempoTotalLogado()).isNull();
    }

    @Test
    public void getTempoTotalLogado_null_quandoNaoHouverHorarioDeLogin() {
        var response = umaLoginLogoutResponse(null, "20:20:42");
        assertThat(response.getTempoTotalLogado()).isNull();
    }

    @Test
    public void getTempoTotalLogado_null_quandoNaoHouverHorarioDeLoginNemDeLogout() {
        var response = umaLoginLogoutResponse(null, null);
        assertThat(response.getTempoTotalLogado()).isNull();
    }

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

    @Test
    public void of_responsesComDadosCorretos_quandoDiferentesColaboradores() {
        var acessos = List.of(
            umUsuarioAcesso("Maria Letícia", "2020-06-24T11:30:06", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T13:00:00", Eboolean.V),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T17:45:57", Eboolean.F),
            umUsuarioAcesso("Maria Letícia", "2020-06-24T21:10:30", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T12:15:40", Eboolean.F),
            umUsuarioAcesso("João Pedro", "2020-06-24T12:50:31", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T13:00:40", null),
            umUsuarioAcesso("João Pedro", "2020-06-24T23:00:00", Eboolean.V),
            umUsuarioAcesso("João Pedro", "2020-06-24T14:15:40", null)
        );

        assertThat(LoginLogoutResponse.of(acessos))
            .extracting("colaborador", "login", "logout")
            .containsExactlyInAnyOrder(
                umaResponseTuple("Maria Letícia", "11:30:06", "13:00:00"),
                umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30"),
                umaResponseTuple("João Pedro", "12:15:40", "12:50:31"),
                umaResponseTuple("João Pedro", null, "23:00:00")
            );

        IntStream.rangeClosed(1, 200).forEach(seed -> {
            var acessosEmbaralhados = ListUtil.toShuffledList(acessos, new Random(seed));
            assertThat(LoginLogoutResponse.of(acessosEmbaralhados))
                .extracting("colaborador", "login", "logout")
                .containsExactlyInAnyOrder(
                    umaResponseTuple("Maria Letícia", "11:30:06", "13:00:00"),
                    umaResponseTuple("Maria Letícia", "17:45:57", "21:10:30"),
                    umaResponseTuple("João Pedro", "12:15:40", "12:50:31"),
                    umaResponseTuple("João Pedro", null, "23:00:00")
                );
        });
    }

    private LoginLogoutResponse umaLoginLogoutResponse(String loginTime, String logoutTime) {
        return LoginLogoutResponse.builder()
            .login(getLocalTime(loginTime))
            .logout(getLocalTime(logoutTime))
            .build();
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
