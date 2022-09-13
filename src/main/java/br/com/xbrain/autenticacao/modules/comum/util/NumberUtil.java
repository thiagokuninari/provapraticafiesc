package br.com.xbrain.autenticacao.modules.comum.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberUtil {

    public static List<Integer> getIntsRangeList(int startInclusive, int endInclusive) {
        return IntStream.rangeClosed(startInclusive, endInclusive)
            .boxed()
            .collect(Collectors.toList());
    }
}
