package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeUfHelper.cidadesUfs;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeUfHelper.cidadesUfsParaTestePartition;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadePredicateTest {

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
}
