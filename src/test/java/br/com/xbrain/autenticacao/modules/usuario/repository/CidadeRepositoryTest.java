package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CidadeRepositoryTest {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    public void findCodigoIbgeRegionalByCidade_deveRetornarVazio_quandoInformarListaComCidadeIdNaoExistente() {
        var predicate = new CidadePredicate()
            .comCidadeId(List.of(123123, 213213))
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate))
            .isEmpty();
    }

    @Test
    public void findCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadeId() {
        var predicate = new CidadePredicate()
            .comCidadeId(List.of(3426, 5578))
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome")
            .containsExactlyInAnyOrder(
                tuple(3426, "MARINGA", "4115200", 1027, "RPS"),
                tuple(5578, "LONDRINA", "4113700", 1027, "RPS"));
    }

    @Test
    public void findCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarVazio_quandoInformarListaComValoresInexistentes() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("SP", "MG"))
            .build();

        var predicate = new CidadePredicate()
            .comCidadesUfs(listaCidadesUfs)
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidadeNomeAndUf(predicate)).isEmpty();
    }

    @Test
    public void findCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrar() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("PR", "PB", "RN"))
            .build();

        var predicate = new CidadePredicate()
            .comCidadesUfs(listaCidadesUfs)
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidadeNomeAndUf(predicate))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId",
                "regionalNome", "ufId", "estadoNome", "uf")
            .hasSize(3)
            .containsExactly(
                tuple(2641, "CARAUBAS", "2504074", 1, "LESTE", 24, "PARAIBA", "PB"),
                tuple(5578, "LONDRINA", "4113700", 3, "SUL", 1, "PARANA", "PR"),
                tuple(5604, "CARAUBAS", "2402303", 1, "LESTE", 26, "RIO GRANDE DO NORTE", "RN"));
    }

    @Test
    public void buscarCidadeDistrito_deveRetornarCidade_quandoInformarUfAndNomeCidadeAndNomeDistritoCorretos() {
        assertThat(cidadeRepository
                .buscarCidadeDistrito("PR", "LONDRINA", "SAO LUIZ").get())
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(30848, "SAO LUIZ", "PARANA", "PR", 5578);
    }
}
