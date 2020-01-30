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
            .hasSize(5)
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
            .contains(
                tuple(3237, "ARAPONGAS", 189, "LONDRINA", 45, "NORTE DO PARANÁ", 20, "NORTE DO PARANÁ", 3, "SUL"),
                tuple(1443, "BELO VALE", 95, "BRI - ITAÚNA - MG", 21, "METROPOLITANA BH", 6, "MINAS GERAIS", 1, "LESTE"),
                tuple(2466, "BELTERRA", 33, "BRI - SANTARÉM - PA", 9, "PA/AP", 2, "NORTE", 1, "LESTE"),
                tuple(3022, "BENEDITINOS", 42, "REMOTO - PIAUÍ", 11, "PIAUÍ", 3, "FORTALEZA", 1, "LESTE"),
                tuple(2617, "BERNARDINO BATISTA", 55, "BRI - SOUSA - PB", 14, "PARAÍBA", 4, "NORDESTE", 1, "LESTE"));
    }

    @Test
    public void findCidadesDtoByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesDtoByUsuarioId(999)).isEmpty();
    }
}