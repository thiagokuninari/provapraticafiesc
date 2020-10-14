package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql({"classpath:/tests_database.sql", "classpath:/tests_sites.sql"})
public class SiteRepositoryTest {

    @Autowired
    private SiteRepository repository;

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoNaoExistirCidadesVinculadas() {
        assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(1, 2), 100))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoExistirCidadesVinculadasEIdForDiferente() {
        assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(5578), 100))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_deveRetornarUmSite_quandoExistirCidadesVinculadasNele() {
        assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(5578), 0))
            .isPresent();
    }

    @Test
    public void findBySituacaoAtiva_listaComTresSites_quandoBuscarSitesComSituacaoAtiva() {
        assertThat(repository.findBySituacaoAtiva(new SitePredicate().build()))
            .hasSize(5);
    }

    @Test
    public void findByEstadoId_listaComDoisSites_quandoBuscarSitesPeloEstadoId() {
        assertThat(repository.findByEstadoId(2))
            .hasSize(2);
    }

    @Test
    public void removeDiscadoraNoSite_void_quandoSitePossuirDiscadora() {
        assertThat(repository.findById(102).orElseThrow())
                .hasFieldOrPropertyWithValue("discadoraId", 8);

        repository.removeDiscadoraBySite(102);

        repository.flush();

        assertThat(repository.findOne(102))
                .hasFieldOrPropertyWithValue("discadoraId", null);
    }

    @Test
    public void adicionaDiscadoraNoSite_void_quandoSitePossuirDiscadora() {
        assertThat(repository.findById(100).orElseThrow())
                .hasFieldOrPropertyWithValue("discadoraId", null);

        repository.updateDiscadoraBySites(12, List.of(100));

        repository.flush();

        assertThat(repository.findOne(100))
                .hasFieldOrPropertyWithValue("discadoraId", 12);
    }

    @Test
    public void findSupervisoresBySiteIdAndUsuarioSuperiorId_deveListarSupervisores_quandoRespeitarSiteIdAndUsuarioSuperiorId() {
        assertThat(repository.findSupervisoresBySiteIdAndUsuarioSuperiorId(100, 405))
            .extracting("id", "nome")
            .containsExactly(
                Tuple.tuple(406, "CARLOS")
            );
    }
}
