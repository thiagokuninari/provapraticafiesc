package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.CodigoIbgeRegionalResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Import({CidadeService.class})
@Transactional
@Sql(scripts = {"classpath:/tests_cidade.sql"})
public class CidadeServiceIT {

    @Autowired
    private CidadeService service;

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaVaziaDeCidadesId() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of()))
            .hasSize(0);
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of()))
            .isEqualTo(new ArrayList<CodigoIbgeRegionalResponse>());
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaComCidadeIdNaoExistente() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(123123, 213213)))
            .hasSize(0);
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(123123, 213213)))
            .isEqualTo(new ArrayList<CodigoIbgeRegionalResponse>());
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadeId() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(5578, 3426, 4498)))
            .hasSize(3);
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(5578, 3426, 4498)))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome")
            .containsExactlyInAnyOrder(
                tuple(5578, "LONDRINA", "4113700", 1027, "RPS"),
                tuple(3426, "MARINGA", "4115200", 1027, "RPS"),
                tuple(4498, "CHAPECO", "4204202", 1027, "RPS"));
    }
}
