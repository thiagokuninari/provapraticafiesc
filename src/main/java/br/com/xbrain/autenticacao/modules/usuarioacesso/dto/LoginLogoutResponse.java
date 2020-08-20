package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginLogoutResponse {

    private String colaborador;
    private LocalTime login;
    private LocalTime logout;

    public String getTempoTotalLogado() {
        if (Objects.isNull(login) || Objects.isNull(logout)) {
            return null;
        }
        var tempoTotalLogado = Duration.between(login, logout);
        if (tempoTotalLogado.isNegative()) {
            return null;
        }
        return DurationFormatUtils.formatDuration(tempoTotalLogado.toMillis(), "HH:mm:ss");
    }

    public static List<LoginLogoutResponse> of(Collection<UsuarioAcesso> acessos) {
        var responses = Stream.<LoginLogoutResponse>builder();

        var responseRef = new AtomicReference<>(new LoginLogoutResponse());
        var ultimoFlagLogout = new AtomicReference<String>();

        acessos.stream()
            .sorted(Comparator.comparing(UsuarioAcesso::getDataCadastro))
            .filter(acesso -> Objects.nonNull(acesso.getFlagLogout()))
            .forEach(acesso -> {
                if (Objects.equals(acesso.getFlagLogout(), "F")) {
                    addNovoLoginLogout(responses, responseRef, acesso).setLogin(acesso.getDataCadastro().toLocalTime());
                } else if (Objects.equals(acesso.getFlagLogout(), "V")) {
                    if (!Objects.equals(ultimoFlagLogout.get(), "F")) {
                        addNovoLoginLogout(responses, responseRef, acesso);
                    }
                    responseRef.get().setLogout(acesso.getDataCadastro().toLocalTime());
                }
                ultimoFlagLogout.set(acesso.getFlagLogout());
            });

        return responses.build().collect(Collectors.toList());
    }

    private static LoginLogoutResponse addNovoLoginLogout(
        Stream.Builder<LoginLogoutResponse> responses,
        AtomicReference<LoginLogoutResponse> responseRef,
        UsuarioAcesso acesso) {
        var novoLoginLogout = new LoginLogoutResponse();
        responseRef.set(novoLoginLogout);
        responses.add(novoLoginLogout);
        novoLoginLogout.setColaborador(acesso.getUsuario().getNome());
        return novoLoginLogout;
    }
}
