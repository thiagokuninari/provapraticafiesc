package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
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
    public void findAllByPredicate_deveRetornarListaTodasAsCidades_quandoSolicitado() {
        assertThat(cidadeRepository.findAllByPredicate(new CidadePredicate().build()))
            .hasSize(108);
    }

    @Test
    public void findAllByPredicate_deveRetornarListaTodasAsCidadesSemDistritos_quandoSolicitado() {
        var predicate = new CidadePredicate()
            .comDistritos(Eboolean.F)
            .build();

        assertThat(cidadeRepository.findAllByPredicate(predicate))
            .hasSize(45);
    }

    @Test
    public void findAllByPredicate_deveRetornarListaTodosOsDistritosSemCidades_quandoSolicitado() {
        var predicate = new CidadePredicate()
            .comDistritos(Eboolean.V)
            .build();

        assertThat(cidadeRepository.findAllByPredicate(predicate))
            .hasSize(63);
    }

    @Test
    public void findAllByPredicate_deveRetornarCidade_quandoEncontrarPorNome() {
        var predicate = new CidadePredicate()
            .comNome("LONDRINA")
            .build();

        assertThat(cidadeRepository.findAllByPredicate(predicate))
            .hasSize(1)
            .extracting("id", "nome", "codigoIbge", "fkCidade")
            .containsExactly(tuple(5578, "LONDRINA", "4113700", null));
    }

    @Test
    public void findAllByPredicate_deveRetornarListaCidades_quandoEncontrarPorUf() {
        var predicate = new CidadePredicate()
            .comUf("SP")
            .build();

        assertThat(cidadeRepository.findAllByPredicate(predicate))
            .hasSize(20)
            .extracting("nome")
            .containsExactly(
                "ALDEIA",
                "AMADEU AMARAL",
                "AVENCAS",
                "BARUERI",
                "BERNARDINO DE CAMPOS",
                "CAJAMAR",
                "COSMOPOLIS",
                "COSMORAMA",
                "DIRCEU",
                "GUAPIRANGA",
                "JARDIM BELVAL",
                "JARDIM SILVEIRA",
                "JORDANESIA",
                "LACIO",
                "LINS",
                "MARILIA",
                "OSASCO",
                "PADRE NOBREGA",
                "POLVILHO",
                "ROSALIA"
            );
    }
}
