package br.com.xbrain.autenticacao.modules.usuarioacesso.enums;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes"})
@Getter
@AllArgsConstructor
public enum ERelatorioLoginLogoutSort {
    COLABORADOR("colaborador", LoginLogoutResponse::getColaboradorUpperCase),
    LOGIN("login", LoginLogoutResponse::getLogin),
    LOGOUT("logout", LoginLogoutResponse::getLogout),
    TEMPO_TOTAL_LOGADO("tempoTotalLogado", LoginLogoutResponse::getTempoTotalLogado);

    private String key;
    private Function<LoginLogoutResponse, ? extends Comparable> keyFunction;

    public static Page<LoginLogoutResponse> getPage(Collection<LoginLogoutResponse> responses, PageRequest pageRequest) {
        var content = responses.stream()
            .sorted(getComparator(pageRequest))
            .skip(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .collect(Collectors.toList());
        return new PageImpl<>(content, pageRequest, responses.size());
    }

    private <T> Comparator<T> getComparator() {
        return Comparator.comparing(keyFunction, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static Comparator<LoginLogoutResponse> getComparator(PageRequest pageRequest) {
        var sort = Stream.of(values())
            .filter(s -> Objects.equals(s.key, pageRequest.getOrderBy()))
            .findFirst()
            .orElse(LOGIN);
        var direction = Sort.Direction.fromStringOrNull(pageRequest.getOrderDirection());
        return getComparator(sort.getComparator(), direction);
    }

    private static Comparator<LoginLogoutResponse> getComparator(
        Comparator<LoginLogoutResponse> comparator,
        Sort.Direction direction) {
        return direction == Sort.Direction.DESC ? comparator.reversed() : comparator;
    }
}
