package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.BRT;
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
        assertThat(repository.findFirstBySituacaoAndCidadesIdInAndIdNot(ESituacao.A, List.of(1, 2), 100))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoExistirCidadesVinculadasEIdForDiferente() {
        assertThat(repository.findFirstBySituacaoAndCidadesIdInAndIdNot(ESituacao.A, List.of(5578), 100))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_deveRetornarUmSite_quandoExistirCidadesVinculadasNele() {
        assertThat(repository.findFirstBySituacaoAndCidadesIdInAndIdNot(ESituacao.A, List.of(5578), 0))
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
    public void findTop1ByPredicate_optionalVazio_seBuscarPorCodigoCidadeDbmENaoHouverResultados() {
        assertThat(repository.findTop1ByPredicate(
            new SitePredicate()
                .comCidade("CASCAVEL")
                .comUf("PR")
                .todosSitesAtivos()
                .build()))
            .isEmpty();
    }

    @Test
    public void findTop1ByPredicate_optionalSite_seBuscarPorCodigoCidadeDbmEHouverResultados() {
        var site = repository.findTop1ByPredicate(
            new SitePredicate()
                .comCidade("LONDRINA")
                .comUf("PR")
                .todosSitesAtivos()
                .build());

        assertThat(site)
            .isNotEmpty();
        assertThat(site.get())
            .extracting("id", "nome", "timeZone", "estados", "cidades", "supervisores", "coordenadores",
                "situacao", "discadoraId")
            .containsExactly(100, "São Paulo", BRT, Set.of(umaUf(1, "PARANA", "PR")), Set.of(umaCidade(5578)),
                Set.of(umUsuario(102)), Set.of(umUsuario(300)), ESituacao.A, null);
    }

    @Test
    public void findTop1ByPredicate_optionalVazio_seBuscarPorCidadeUfENaoHouverResultados() {
        assertThat(repository.findTop1ByPredicate(
            new SitePredicate()
                .comCodigoCidadeDbm(1)
                .todosSitesAtivos()
                .build()))
            .isEmpty();
    }

    @Test
    public void findTop1ByPredicate_optionalSite_seBuscarPorCidadeUfEHouverResultados() {
        var site = repository.findTop1ByPredicate(
            new SitePredicate()
                .comCodigoCidadeDbm(3)
                .todosSitesAtivos()
                .build());

        assertThat(site)
            .isNotEmpty();
        assertThat(site.get())
            .extracting("id", "nome", "timeZone", "estados", "cidades", "supervisores", "coordenadores",
                "situacao", "discadoraId")
            .containsExactly(100, "São Paulo", BRT, Set.of(umaUf(1, "PARANA", "PR")), Set.of(umaCidade(5578)),
                Set.of(umUsuario(102)), Set.of(umUsuario(300)), ESituacao.A, null);
    }

    private Uf umaUf(Integer id, String nome, String uf) {
        return Uf.builder()
            .id(id)
            .nome(nome)
            .uf(uf)
            .build();
    }

    private Cidade umaCidade(Integer id) {
        return Cidade.builder()
            .id(id)
            .build();
    }

    private Usuario umUsuario(Integer id) {
        return Usuario.builder()
            .id(id)
            .build();
    }
}
