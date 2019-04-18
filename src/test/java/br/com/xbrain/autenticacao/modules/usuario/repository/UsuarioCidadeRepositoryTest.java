package br.com.xbrain.autenticacao.modules.usuario.repository;

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
}