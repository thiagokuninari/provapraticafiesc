package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:/tests_subnivel.sql")
public class SubNivelRepositoryTest {

    @Autowired
    private SubNivelRepository repository;

    @Test
    public void findFuncionalidadesIdsByNivelId_deveRetornarListaDeFuncionalidadesIds_quandoNivelForIgual() {
        assertThat(repository.findFuncionalidadesIdsByNivelId(111111))
            .containsExactlyInAnyOrder(111111, 222222, 333333, 555555);
    }

    @Test
    public void findFuncionalidadesIdsByNivelId_deveRetornarListaVazia_quandoNaoExistirFuncionalidadesIdsParaONivel() {
        assertThat(repository.findFuncionalidadesIdsByNivelId(3)).isEmpty();
    }
}
