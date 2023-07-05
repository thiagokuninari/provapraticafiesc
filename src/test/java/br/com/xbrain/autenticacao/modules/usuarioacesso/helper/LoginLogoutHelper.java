package br.com.xbrain.autenticacao.modules.usuarioacesso.helper;

import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoginLogoutHelper {

    public static LoginLogoutResponse umLoginLogoutResponse(Integer usuarioId,
                                                            LocalDateTime dataLogin,
                                                            LocalDateTime dataLogout) {

        return LoginLogoutResponse.builder()
            .usuarioId(usuarioId)
            .dataLogin(dataLogin)
            .dataLogout(dataLogout)
            .build();
    }

    public static List<LoginLogoutResponse> umaListaLoginLogoutResponse() {

        return List.of(
            umLoginLogoutResponse(
                1,
                LocalDateTime.of(2021, 8, 5, 11, 18, 0),
                LocalDateTime.of(2021, 8, 5, 12, 18, 0)),
            umLoginLogoutResponse(
                1,
                LocalDateTime.of(2021, 7, 5, 11, 18, 0),
                LocalDateTime.of(2021, 7, 8, 10, 18, 0)),
            umLoginLogoutResponse(
                2,
                LocalDateTime.of(2021, 8, 5, 11, 18, 0),
                null)
        );
    }

    public static RelatorioLoginLogoutRequest umRelatorio() {
        return RelatorioLoginLogoutRequest.builder()
            .dataInicial(LocalDate.of(2023, 6, 10))
            .dataFinal(LocalDate.of(2023, 6, 20))
            .usuariosIds(Lists.newArrayList(1, 2, 3))
            .build();
    }
}
