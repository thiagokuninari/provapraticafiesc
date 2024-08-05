package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeUfHelper.cidadesUfs;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeUfHelper.cidadesUfsParaTestePartition;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadePredicateTest {

    @Test
    public void comNome_deveMontarPredicate_quandoSolicitado() {
        assertThat(new CidadePredicate().comNome("LONDRINA").build())
            .isEqualTo(new BooleanBuilder(cidade.nome.likeIgnoreCase("LONDRINA")));
    }

    @Test
    public void comNome_naoDeveMontarPredicate_quandoNomeForNull() {
        assertThat(new CidadePredicate().comNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comNome_naoDeveMontarPredicate_quandoNomeForVazio() {
        assertThat(new CidadePredicate().comNome("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comNome_naoDeveMontarPredicate_quandoNomeConterEspacos() {
        assertThat(new CidadePredicate().comNome("   ").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_deveMontarPredicate_quandoSolicitado() {
        assertThat(new CidadePredicate().comUf("PR").build())
            .isEqualTo(new BooleanBuilder(cidade.uf.uf.likeIgnoreCase("PR")));
    }

    @Test
    public void comUf_naoDeveMontarPredicate_quandoUfForNull() {
        assertThat(new CidadePredicate().comUf(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_naoDeveMontarPredicate_quandoUfForVazio() {
        assertThat(new CidadePredicate().comUf("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_naoDeveMontarPredicate_quandoUfConterEspacos() {
        assertThat(new CidadePredicate().comUf("   ").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUfId_deveMontarPredicate_quandoSolicitado() {
        assertThat(new CidadePredicate().comUfId(1).build())
            .isEqualTo(new BooleanBuilder(cidade.uf.id.eq(1)));
    }

    @Test
    public void comUfId_naoDeveMontarPredicate_quandoUfIdForNull() {
        assertThat(new CidadePredicate().comUfId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comRegionalId_deveMontarPredicate_quandoSolicitado() {
        assertThat(new CidadePredicate().comRegionalId(1027).build())
            .isEqualTo(new BooleanBuilder(cidade.regional.id.eq(1027)));
    }

    @Test
    public void comRegionalId_naoDeveMontarPredicate_quandoRegionalIdForNull() {
        assertThat(new CidadePredicate().comRegionalId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDistritos_deveMontarPredicate_quandoEbooleanForV() {
        assertThat(new CidadePredicate().comDistritos(Eboolean.V).build())
            .isEqualTo(new BooleanBuilder(cidade.fkCidade.isNotNull()));
    }

    @Test
    public void comDistritos_deveMontarPredicate_quandoEbooleanForF() {
        assertThat(new CidadePredicate().comDistritos(Eboolean.F).build())
            .isEqualTo(new BooleanBuilder(cidade.fkCidade.isNull()));
    }

    @Test
    public void comDistritos_naoDeveMontarPredicate_quandoEbooleanForNull() {
        assertThat(new CidadePredicate().comDistritos(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidadesId_deveFiltrarPorIds_quandoIdsListaComElementos() {
        assertThat(new CidadePredicate().comCidadesId(List.of(230, 100, 340)).build())
            .isEqualTo(new BooleanBuilder(cidade.id.in(230, 100, 340)));
    }

    @Test
    public void comCidadesUfs_deveFiltrarPorCidadeNomeEUf_quandoListasPreenchidasCorretamente() {
        var predicate = new CidadePredicate()
            .comCidadesUfs(cidadesUfs())
            .build();

        var expected = new BooleanBuilder(cidade.nome.in(cidadesUfs().getCidades()))
            .and(cidade.uf.uf.in(cidadesUfs().getUfs()));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCidadesUfs_deveParticionarOsIns_quandoTamanhoListaCidadesForMaiorQueMaximoOracle() {
        var predicate = new CidadePredicate()
            .comCidadesUfs(cidadesUfsParaTestePartition())
            .build();

        var expected = new BooleanBuilder(cidade.nome.in(cidadesUfsParaTestePartition().getCidades().subList(0, 1000))
            .or(cidade.nome.in(cidadesUfsParaTestePartition().getCidades().subList(1000, 1200))))
            .and(cidade.uf.uf.in(cidadesUfsParaTestePartition().getUfs()));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCidadesId_deveParticionarOsIns_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
        var cidadesId = generateRandomIntList(new Random(789), 2700);

        assertThat(new CidadePredicate().comCidadesId(cidadesId).build())
            .isEqualTo(new BooleanBuilder(cidade.id.in(cidadesId.subList(0, 1000)))
                .or(cidade.id.in(cidadesId.subList(1000, 2000)))
                .or(cidade.id.in(cidadesId.subList(2000, 2700))));
    }

    private List<Integer> generateRandomIntList(Random random, int size) {
        return IntStream.range(0, size)
            .map(i -> random.nextInt())
            .boxed()
            .collect(Collectors.toList());
    }

    @Test
    public void comCodigosIbge_deveMontarPredicateComCodigosIbge_quandoSolicitado() {
        var predicate = new CidadePredicate()
            .comCodigosIbge(List.of("ibge")).build();

        assertThat(predicate)
            .isEqualTo(new BooleanBuilder().and(cidade.codigoIbge.in("ibge")));
    }

    @Test
    public void comCodigosIbge_deveIgnorarTodosOsRegistros_quandoCodigosIbgeForVazio() {
        var predicate = new CidadePredicate()
            .comCodigosIbge(List.of())
            .build();

        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }
}
