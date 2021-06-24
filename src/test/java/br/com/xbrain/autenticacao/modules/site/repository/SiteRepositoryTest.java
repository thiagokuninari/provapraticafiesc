package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
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
import static org.junit.Assert.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql({"classpath:/tests_database.sql", "classpath:/tests_sites.sql"})
public class SiteRepositoryTest {

    @Autowired
    private SiteRepository repository;

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoNaoExistirCidadesVinculadas() {
        assertThat(repository.findAll(umSitePredicate(ESituacao.A, List.of(1, 2), 100)))
            .isEmpty();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoExistirCidadesVinculadasEIdForDiferente() {
        assertThat(repository.findAll(umSitePredicate(ESituacao.A, List.of(5578), 100)))
            .isEmpty();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_deveRetornarUmSite_quandoExistirCidadesVinculadasNele() {
        assertThat(repository.findAll(umSitePredicate(ESituacao.A, List.of(5578), 0)))
            .isNotEmpty();
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
    public void ignoraSite_deveIgnorarSitePorId() {
        var sitesComSiteIgnorado = repository.findAll(new SitePredicate().ignorarSite(100).build());
        Assertions.assertThat(sitesComSiteIgnorado).extracting(Site::getId, Site::getNome)
            .doesNotContain(Tuple.tuple(100, "São Paulo"));
    }

    @Test
    public void findBySupervisorId_siteSp_quandoEncontrarSiteVinculadoAoSupervisorPeloId() {
        assertThat(repository.findBySupervisorId(102))
            .extracting("id", "nome")
            .contains(100, "São Paulo");
    }

    @Test
    public void findSupervisor_deveIgnorarSupervisorDeSitesInativos() {
        var site = repository.findById(105).get();

        assertEquals(site.getSituacao(), ESituacao.I);
        assertThat(site.getSupervisores())
            .extracting(Usuario::getId)
            .contains(400);
        assertThat(repository.findBySupervisorId(400))
            .extracting("situacao")
            .contains(ESituacao.A);
    }

    @Test
    public void findSiteCidadeTop1ByPredicate_optionalVazio_seBuscarPorCodigoCidadeDbmENaoHouverResultados() {
        assertThat(repository.findSiteCidadeTop1ByPredicate(
            new SitePredicate()
                .comCidade("CASCAVEL")
                .comUf("PR")
                .todosSitesAtivos()
                .build()))
            .isEmpty();
    }

    @Test
    public void findSiteCidadeTop1ByPredicate_optionalSiteCidade_seBuscarPorCodigoCidadeDbmEHouverResultados() {
        var site = repository.findSiteCidadeTop1ByPredicate(
            new SitePredicate()
                .comCidade("LONDRINA")
                .comUf("PR")
                .todosSitesAtivos()
                .build());

        assertThat(site)
            .isNotEmpty();
        assertThat(site.get())
            .extracting("siteId", "siteNome", "cidadeId", "cidadeNome", "ufId", "ufNome")
            .containsExactly(100, "São Paulo", 5578, "LONDRINA", 1, "PR");
    }

    @Test
    public void findSiteCidadeTop1ByPredicate_optionalVazio_seBuscarPorCidadeUfENaoHouverResultados() {
        assertThat(repository.findSiteCidadeTop1ByPredicate(
            new SitePredicate()
                .comCodigoCidadeDbm(1)
                .todosSitesAtivos()
                .build()))
            .isEmpty();
    }

    @Test
    public void findSiteCidadeTop1ByPredicate_optionalSiteCidade_seBuscarPorCidadeUfEHouverResultados() {
        var site = repository.findSiteCidadeTop1ByPredicate(
            new SitePredicate()
                .comCodigoCidadeDbm(3)
                .todosSitesAtivos()
                .build());

        assertThat(site)
            .isNotEmpty();
        assertThat(site.get())
            .extracting("siteId", "siteNome", "cidadeId", "cidadeNome", "ufId", "ufNome")
            .containsExactly(100, "São Paulo", 5578, "LONDRINA", 1, "PR");
    }

    private Predicate umSitePredicate(ESituacao situacao, List<Integer> cidadeIds, Integer id) {
        return new SitePredicate()
            .comSituacao(situacao)
            .comCidades(cidadeIds)
            .excetoId(id)
            .build();
    }
}
