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
            .extracting("idUf", "nomeUf", "idCidade", "nomeCidade")
            .containsExactlyInAnyOrder(
                tuple(1, "PARANA", 3237, "ARAPONGAS"),
                tuple(8, "MINAS GERAIS", 1443, "BELO VALE"),
                tuple(4, "PARA", 2466, "BELTERRA"),
                tuple(12, "PIAUI", 3022, "BENEDITINOS"),
                tuple(24, "PARAIBA", 2617, "BERNARDINO BATISTA"));
    }

    @Test
    public void findCidadesDtoByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesDtoByUsuarioId(999)).isEmpty();
    }
}