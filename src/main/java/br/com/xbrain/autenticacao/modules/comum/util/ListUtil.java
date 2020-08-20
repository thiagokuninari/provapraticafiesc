package br.com.xbrain.autenticacao.modules.comum.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ListUtil {

    public static <T> List<List<T>> divideListaEmListasMenores(List<T> lista, final int sizeListasMenores) {
        /*TODO - refatorar java 8*/
        List<List<T>> parts = new ArrayList<>();
        final int N = lista.size();
        for (int i = 0; i < N; i += sizeListasMenores) {
            parts.add(new ArrayList<>(
                    lista.subList(i, Math.min(N, i + sizeListasMenores)))
            );
        }
        return parts;
    }

    public static <T> List<T> toShuffledList(List<T> source, Random random) {
        var list = Lists.newArrayList(source);
        Collections.shuffle(list, random);
        //noinspection SimplifyStreamApiCallChains
        return list.stream().collect(Collectors.toList());
    }
}
