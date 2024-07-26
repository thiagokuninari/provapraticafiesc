package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListUtilTest {

    @Test
    public void divideListaEmListasMenores_deveRetornarListasDivididas_quandoInformadoAListaEQuantidadeDeLista() {
        var list = umaListaDeStrings();

        assertThat(list)
            .isEqualTo(
                List.of("Pluto", "Ariana Grande", "McDonalds", "Disney")
            );

        assertThat(ListUtil.divideListaEmListasMenores(list, 2))
            .isEqualTo(List.of(
                List.of("Pluto", "Ariana Grande"),
                List.of("McDonalds", "Disney"))
            );
    }

    @Test
    public void toShuffledList_deveRetornarListaAleatoria_quandoFornecidoLista() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.toShuffledList(list, new Random()))
            .hasSize(4)
            .containsExactlyInAnyOrder("Pluto", "Ariana Grande", "McDonalds", "Disney");
    }

    @Test
    public void getElement_elementoNaPosicaoDaLista_quandoPosicaoDentroDoLimite() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, 1)).isPresent().get().isEqualTo("Ariana Grande");
    }

    @Test
    public void getElement_elementoNaPosicaoDaLista_quandoPosicaoPrimeiroElemento() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, 0)).isPresent().get().isEqualTo("Pluto");
    }

    @Test
    public void getElement_elementoNaPosicaoDaLista_quandoPosicaoUltimoElemento() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, list.size() - 1)).isPresent().get().isEqualTo("Disney");
    }

    @Test
    public void getElement_optionalVazio_quandoPosicaoAcimaDoUltimoElemento() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, 10)).isNotPresent();
    }

    @Test
    public void getElement_optionalVazio_quandoPosicaoIgualAoTamanhoDaLista() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, list.size())).isNotPresent();
    }

    @Test
    public void getElement_optionalVazio_quandoPosicaoNegativa() {
        var list = umaListaDeStrings();

        assertThat(ListUtil.getElement(list, -9)).isNotPresent();
        assertThat(ListUtil.getElement(list, -1)).isNotPresent();
    }

    @Test
    public void getElement_optionalVazio_quandoElementoNaPosicaoDaListaForNull() {
        var list = Stream.of("Um", null, "Dois").collect(Collectors.toList());

        assertThat(ListUtil.getElement(list, 1)).isNotPresent();
    }

    private List<String> umaListaDeStrings() {
        return List.of("Pluto", "Ariana Grande", "McDonalds", "Disney");
    }
}
