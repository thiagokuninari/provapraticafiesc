package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_cidade.sql"})
public class CidadeServiceTest {

    @Autowired
    private CidadeService service;

    @Test
    public void getCidadeByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() {
        assertThat(service.getCidadeByCodigoCidadeDbm(3))
            .extracting("id",
                "siteId",
                "nome",
                "uf")
            .containsExactly(5578, 100, "LONDRINA", "PR");
    }

    @Test
    public void getCidadeByCodigoCidadeDbm_deveRetornarException_quandoNaoExistirCidadeComCodigoCidadeDbm() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getCidadeByCodigoCidadeDbm(4))
            .withMessage("Cidade não encontrada.");
    }

    @Test
    public void findCidadeComSiteByUfECidade_deveRetornarCidade_quandoExistir() {
        assertThat(service.findCidadeComSiteByUfECidade("PR", "LONDRINA"))
            .extracting("id",
                "siteId",
                "nome",
                "uf")
            .containsExactly(5578, 100, "LONDRINA", "PR");
    }

    @Test
    public void findCidadeComSiteByUfECidade_deveRetornarException_quandoNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findCidadeComSiteByUfECidade("LONDRINA", "PI"))
            .withMessage("Cidade não encontrada.");
    }
}