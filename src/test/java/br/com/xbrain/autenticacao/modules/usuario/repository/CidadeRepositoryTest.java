package br.com.xbrain.autenticacao.modules.usuario.repository;

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
            .comCidadesId(List.of(123123, 213213))
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate))
            .isEmpty();
    }

    @Test
    public void findCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadeId() {
        var predicate = new CidadePredicate()
            .comCidadesId(List.of(3426, 5578))
            .build();

        assertThat(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome")
            .containsExactlyInAnyOrder(
                tuple(3426, "MARINGA", "4115200", 1027, "RPS"),
                tuple(5578, "LONDRINA", "4113700", 1027, "RPS"));
    }

    @Test
    public void findAllCidades_deveRetornarListaDeTodasAsCidadesDoPais_seSolicitado() {
        assertThat(cidadeRepository.findAllCidades())
            .extracting("id", "nome", "uf.id")
            .contains(
                tuple(3237, "ARAPONGAS", 1),
                tuple(1443, "BELO VALE", 8),
                tuple(2466, "BELTERRA", 4),
                tuple(3022, "BENEDITINOS", 12));
    }
}
