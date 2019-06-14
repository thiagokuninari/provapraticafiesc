package br.com.xbrain.autenticacao.modules.comum.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_area_atuacao.sql"})
public class RegionalServiceTest {

    @Autowired
    private RegionalService regionalService;

    @Test
    public void getAllByUsuarioId_deveRetornarRegionaisSulESp_doUsuarioInformadoPeloParametro() {
        assertThat(regionalService.getAllByUsuarioId(1))
                .isNotNull()
                .extracting("value", "label")
                .containsExactly(tuple(3, "SUL"), tuple(2, "SÃO PAULO"));
    }

    @Test
    public void getAllByUsuarioId_deveRetornarRegionalLeste_doUsuarioInformadoPeloParametro() {
        assertThat(regionalService.getAllByUsuarioId(2))
                .isNotNull()
                .extracting("value", "label")
                .containsExactly(tuple(1, "LESTE"));
    }
}
