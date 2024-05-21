package br.com.xbrain.autenticacao.modules.comum.util;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamUtils {
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <T, C> C mapNull(T value, Function<T, C> function, C backup) {
        return Optional.ofNullable(value).map(function).orElse(backup);
    }
}
