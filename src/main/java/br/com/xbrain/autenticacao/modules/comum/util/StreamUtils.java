package br.com.xbrain.autenticacao.modules.comum.util;

import java.util.Set;
import java.util.function.Consumer;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class StreamUtils {
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <T> UnaryOperator<T> peek(Consumer<T> consumer) {
        return obj -> {
            consumer.accept(obj);
            return obj;
        };
    }

    public static <T, C> C mapNull(T value, Function<T, C> function, C backup) {
        return (value != null) ? function.apply(value) : backup;
    }

    public static <T, C> C mapNull(T value, Function<T, C> function) {
        return (value != null) ? function.apply(value) : null;
    }
}
