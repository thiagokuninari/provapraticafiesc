package br.com.xbrain.autenticacao.modules.comum.util;

import java.util.ArrayList;
import java.util.List;

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
}
