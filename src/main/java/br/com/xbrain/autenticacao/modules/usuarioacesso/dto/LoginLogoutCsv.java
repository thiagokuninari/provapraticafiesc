package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.xbrainutils.DateUtils;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogoutCsv {

    private static final String CSV_COL_DELIMITER = ";";

    private String colaborador;
    private String data;
    private List<LocalTime> logins;
    private List<LocalTime> logouts;

    public int getQuantidadeDeLogouts() {
        return logouts.size();
    }

    public String getTempoTotalLogado() {
        var tempoTotalLogado = IntStream.range(0, getLoginLogoutsCount())
            .mapToLong(i -> {
                var login = ListUtil.getElement(logins, i);
                var logout = ListUtil.getElement(logouts, i);
                if (login.isPresent() && logout.isPresent()) {
                    var duracao = Duration.between(login.get(), logout.get());
                    return !duracao.isNegative() ? duracao.toMillis() : 0L;
                }
                return 0L;
            })
            .sum();
        return DurationFormatUtils.formatDuration(tempoTotalLogado, "HH:mm:ss");
    }

    public static String getCsvHeader(Collection<LoginLogoutCsv> csvs) {
        var cols = Stream.<String>builder();
        Stream.of("COLABORADOR", "DATA", "QUANTIDADE DE LOGOUT").forEach(cols::add);
        IntStream.rangeClosed(1, getLoginLogoutsCount(csvs))
            .forEach(i -> {
                cols.add("HORÁRIO LOGIN " + i);
                cols.add("HORÁRIO LOGOUT " + i);
            });
        cols.add("TEMPO TOTAL LOGADO");
        return cols.build().collect(Collectors.joining(CSV_COL_DELIMITER));
    }

    public static String getCsvRows(Collection<LoginLogoutCsv> csvs) {
        return csvs.stream().map(csv -> {
            var cols = Stream.builder();
            Stream.of(csv.colaborador, csv.data, csv.getQuantidadeDeLogouts()).forEach(cols::add);
            IntStream.range(0, getLoginLogoutsCount(csvs))
                .forEach(i -> {
                    cols.add(getLoginLogoutCol(csv.logins, i));
                    cols.add(getLoginLogoutCol(csv.logouts, i));
                });
            cols.add(csv.getTempoTotalLogado());
            return cols.build()
                .map(Object::toString)
                .map(String::toUpperCase)
                .collect(Collectors.joining(CSV_COL_DELIMITER));
        }).collect(Collectors.joining("\n"));
    }

    public static String getCsv(Collection<LoginLogoutCsv> csvs) {
        return getCsvHeader(csvs) + "\n" + getCsvRows(csvs);
    }

    public static List<LoginLogoutCsv> of(List<UsuarioAcesso> responses) {
        var csvs = Stream.<LoginLogoutCsv>builder();

        responses
            .stream()
            .collect(Collectors.groupingBy(UsuarioAcesso::getUsuario))
            .forEach((usuario, acessosUsuario) -> acessosUsuario
                .stream()
                .collect(Collectors.groupingBy(acesso -> acesso.getDataCadastro().toLocalDate()))
                .forEach((data, acessosData) -> csvs.add(of(usuario, data, acessosData))));

        return csvs.build().collect(Collectors.toList());
    }

    private static LoginLogoutCsv of(Usuario usuario, LocalDate data, List<UsuarioAcesso> acessos) {
        var loginLogoutTimesRef = new AtomicReference<>(new LoginLogoutTimes());
        var ultimoFlagLogout = new AtomicReference<String>();
        var loginLogoutsTimes = ImmutableList.<LoginLogoutTimes>builder();

        acessos.stream()
            .sorted(Comparator.comparing(UsuarioAcesso::getDataCadastro))
            .filter(acesso -> Objects.nonNull(acesso.getFlagLogout()))
            .forEach(acesso -> {
                if (Objects.equals(acesso.getFlagLogout(), "F")) {
                    var loginLogoutTimes = addNovoLoginLogoutTimes(loginLogoutsTimes, loginLogoutTimesRef);
                    loginLogoutTimes.login = acesso.getDataCadastro().toLocalTime();
                } else if (Objects.equals(acesso.getFlagLogout(), "V")) {
                    if (!Objects.equals(ultimoFlagLogout.get(), "F")) {
                        addNovoLoginLogoutTimes(loginLogoutsTimes, loginLogoutTimesRef);
                    }
                    loginLogoutTimesRef.get().logout = acesso.getDataCadastro().toLocalTime();
                }
                ultimoFlagLogout.set(acesso.getFlagLogout());
            });

        var loginLogoutsTimesList = loginLogoutsTimes.build();
        return builder()
            .colaborador(usuario.getNome())
            .data(DateUtils.parseLocalDateToString(data))
            .logins(loginLogoutsTimesList.stream().map(loginLogoutTimes -> loginLogoutTimes.login).collect(Collectors.toList()))
            .logouts(loginLogoutsTimesList.stream().map(loginLogoutTimes -> loginLogoutTimes.logout).collect(Collectors.toList()))
            .build();
    }

    private static LoginLogoutTimes addNovoLoginLogoutTimes(
        ImmutableList.Builder<LoginLogoutTimes> loginLogoutsTimes,
        AtomicReference<LoginLogoutTimes> loginLogoutTimesRef) {
        var novoLoginLogoutTimes = new LoginLogoutTimes();
        loginLogoutTimesRef.set(novoLoginLogoutTimes);
        loginLogoutsTimes.add(novoLoginLogoutTimes);
        return novoLoginLogoutTimes;
    }

    private int getLoginLogoutsCount() {
        return Math.max(logins.size(), logouts.size());
    }

    private static int getLoginLogoutsCount(Collection<LoginLogoutCsv> csvs) {
        return csvs.stream()
            .mapToInt(LoginLogoutCsv::getLoginLogoutsCount)
            .max()
            .orElse(0);
    }

    private static String getLoginLogoutCol(List<LocalTime> loginLogout, int index) {
        return ListUtil.getElement(loginLogout, index)
            .map(DateTimeFormatter.ISO_TIME::format)
            .orElse("");
    }

    private static class LoginLogoutTimes {
        private LocalTime login;
        private LocalTime logout;
    }
}
