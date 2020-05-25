package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = "classpath:/usuario-cidade-repository-test.sql")
public class UsuarioCidadeRepositoryTest {
    @Autowired
    private UsuarioCidadeRepository repository;

    @Test
    public void findCidadesIdByUsuarioId_deveRetornarOsIdsDasCidades_quandoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesIdByUsuarioId(100))
            .containsExactlyInAnyOrder(3237, 2466, 1443, 3022, 2617);
    }

    @Test
    public void findCidadesIdByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesIdByUsuarioId(999)).isEmpty();
    }

    @Test
    public void findCidadesDtoByUsuarioId_deveRetornarasCidades_quandoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesDtoByUsuarioId(100))
            .extracting("idRegional", "nomeRegional",
                "idGrupo", "nomeGrupo",
                "idCluster", "nomeCluster",
                "idSubCluster", "nomeSubCluster",
                "idCidade", "nomeCidade")
            .containsExactlyInAnyOrder(
                tuple(3, "SUL", 20, "NORTE DO PARANÁ", 45, "NORTE DO PARANÁ", 189, "LONDRINA", 3237, "ARAPONGAS"),
                tuple(1, "LESTE", 6, "MINAS GERAIS", 21, "METROPOLITANA BH", 95, "BRI - ITAÚNA - MG", 1443, "BELO VALE"),
                tuple(1, "LESTE", 2, "NORTE", 9, "PA/AP", 33, "BRI - SANTARÉM - PA", 2466, "BELTERRA"),
                tuple(1, "LESTE", 3, "FORTALEZA", 11, "PIAUÍ", 42, "REMOTO - PIAUÍ", 3022, "BENEDITINOS"),
                tuple(1, "LESTE", 4, "NORDESTE", 14, "PARAÍBA", 55, "BRI - SOUSA - PB", 2617, "BERNARDINO BATISTA"));
    }

    @Test
    public void findCidadesDtoByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesDtoByUsuarioId(999)).isEmpty();
    }
}