package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CidadeRepositoryIT {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    public void findFirstByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() throws Exception {
        var cidade = cidadeRepository.findByCodigoCidadeDbm(3).get();
        assertEquals("LONDRINA", cidade.getNome());
    }

    @Test
    public void findFirstByCodigoCidadeDbm_deveRetornarOptionalEmpty_quandoNaoExistirCidadeComCodigoCidadeDbm() throws Exception {
        assertThat(cidadeRepository.findByCodigoCidadeDbm(4)).isNotPresent();
    }

}
