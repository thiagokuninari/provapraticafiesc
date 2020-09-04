package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
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
}
