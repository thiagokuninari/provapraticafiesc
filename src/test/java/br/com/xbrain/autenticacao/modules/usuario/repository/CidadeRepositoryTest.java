package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
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

    @Test
    public void buscarCidadeDistrito_deveRetornarCidade_quandoInformarUfAndNomeCidadeAndNomeDistritoCorretos() {
        assertThat(cidadeRepository
                .buscarCidadeDistrito("PR", "LONDRINA", "SAO LUIZ").get())
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(30848, "SAO LUIZ", "PARANA", "PR", 5578);
    }

    @Test
    public void findBySubCluster_deveRetornarCidades_quandoExistirSubCluster() {
        assertThat(cidadeRepository
            .findBySubCluster(1))
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(tuple(3886, "CORUMBIARA", "RONDONIA", "RO", null));
    }

    @Test
    public void findAllByRegionalId_deveRetornarCidades_quandoCidadeTiverRegional() {
        assertThat(cidadeRepository
            .findAllByRegionalId(1030, new CidadePredicate().build()))
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(
                tuple(4864, "BARUERI", "SAO PAULO", "SP", null),
                tuple(4903, "CAJAMAR", "SAO PAULO", "SP", null),
                tuple(5189, "OSASCO", "SAO PAULO", "SP", null));
    }

    @Test
    public void findAllBySubClusterId_deveRetornarCidades_quandoCidadeTiverSubCluster() {
        assertThat(cidadeRepository
            .findAllBySubClusterId(1, new CidadePredicate().build()))
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(
                tuple(3886, "CORUMBIARA", "RONDONIA", "RO", null));
    }

    @Test
    public void getClusterizacao_deveRetornarClusterizacaoDto_quandoCidadesEncontradas() {
        assertThat(cidadeRepository
            .getClusterizacao(3237))
            .extracting("cidadeId", "cidadeNome", "ufId", "ufNome", "regionalNome")
            .containsExactly(
                3237, "ARAPONGAS", 1, "PARANA", "RPS");
    }

    @Test
    public void findFirstByPredicate_deveRetornarCidade_quandoCidadeEncontrada() {
        assertThat(cidadeRepository
            .findFirstByPredicate(new CidadePredicate().build()))
            .get()
            .extracting("id", "nome", "uf.nome", "uf.uf", "fkCidade")
            .containsExactly(46, "CORURIPE", "ALAGOAS", "AL", null);
    }

    @Test
    public void findAllByRegionalIdAndUfId_deveRetornarCidade_quandoCidadesEncontradas() {
        assertThat(cidadeRepository
            .findCidadesByCodigosIbge(new CidadePredicate().build()).size())
            .isEqualTo(45);
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
