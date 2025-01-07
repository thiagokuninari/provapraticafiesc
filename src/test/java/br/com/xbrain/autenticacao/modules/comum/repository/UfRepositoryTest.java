package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UfRepositoryTest {

    @Autowired
    private UfRepository ufRepository;

    @Test
    public void findByOrderByNomeAsc_umaListaDeUfsOrdenadaPeloNome_quandoSolicitado() {
        assertThat(ufRepository.findByOrderByNomeAsc())
                .extracting("nome")
                .containsExactly("ACRE", "ALAGOAS", "AMAPA", "AMAZONAS", "BAHIA", "CEARA", "DISTRITO FEDERAL", "ESPIRITO SANTO",
                        "GOIAS", "MARANHAO", "MATO GROSSO", "MATO GROSSO DO SUL", "MINAS GERAIS", "PARA", "PARAIBA", "PARANA",
                        "PERNAMBUCO", "PIAUI", "RIO DE JANEIRO", "RIO GRANDE DO NORTE", "RIO GRANDE DO SUL", "RONDONIA",
                        "RORAIMA", "SANTA CATARINA", "SAO PAULO", "SERGIPE", "TOCANTINS");
    }

    @Test
    public void buscarEstadosPorRegional_umaListaDeUfsOrdenadaPeloNome_quandoUfTerRegional() {
        assertThat(ufRepository.buscarEstadosPorRegional(1022))
            .extracting("nome")
            .containsExactly("BAHIA", "SERGIPE");
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSitesExcetoPor_deveRetornarUfOrdenadosPorNome_quandoPredicateAplicado() {
        assertThat(ufRepository.buscarEstadosNaoAtribuidosEmSitesExcetoPor(new SitePredicate().build(), 110))
            .extracting("nome")
            .containsExactly("MARANHAO", "PARANA", "PIAUI", "RIO GRANDE DO NORTE", "SANTA CATARINA",
                "SAO PAULO", "TOCANTINS");
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSites_deveRetornarUfNaoAtribuidoOrdenadosPorNome_quandoPredicateAplicado() {
        assertThat(ufRepository.buscarEstadosNaoAtribuidosEmSites(new SitePredicate().build()))
            .extracting("nome")
            .containsExactly("MARANHAO", "PARANA", "PIAUI", "RIO GRANDE DO NORTE", "SANTA CATARINA",
                "SAO PAULO", "TOCANTINS");
    }
}
