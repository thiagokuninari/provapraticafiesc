package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
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
}
