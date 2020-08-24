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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ERelatorioLoginLogoutSort {
    COLABORADOR("colaborador", Comparator.comparing(LoginLogoutResponse::getColaborador, String.CASE_INSENSITIVE_ORDER)),
    LOGIN("login", Comparator.comparing(LoginLogoutResponse::getLogin)),
    LOGOUT("logout", Comparator.comparing(LoginLogoutResponse::getLogout)),
    TEMPO_TOTAL_LOGADO("tempoTotalLogado", Comparator.comparing(LoginLogoutResponse::getTempoTotalLogado));

    private String key;
    private Comparator<LoginLogoutResponse> comparator;

    public static Page<LoginLogoutResponse> getPage(Collection<LoginLogoutResponse> responses, PageRequest pageRequest) {
        var content = responses.stream()
            .sorted(getComparator(pageRequest))
            .skip(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .collect(Collectors.toList());
        return new PageImpl<>(content, pageRequest, content.size());
    }

    private static Comparator<LoginLogoutResponse> getComparator(PageRequest pageRequest) {
        var sort = Stream.of(values())
            .filter(s -> Objects.equals(s.key, pageRequest.getOrderBy()))
            .findFirst()
            .orElse(LOGIN);
        var direction = Sort.Direction.fromStringOrNull(pageRequest.getOrderDirection());
        return getComparator(Comparator.nullsLast(sort.getComparator()), direction);
    }

    private static Comparator<LoginLogoutResponse> getComparator(
        Comparator<LoginLogoutResponse> comparator,
        Sort.Direction direction) {
        return direction == Sort.Direction.DESC ? comparator.reversed() : comparator;
    }
}
