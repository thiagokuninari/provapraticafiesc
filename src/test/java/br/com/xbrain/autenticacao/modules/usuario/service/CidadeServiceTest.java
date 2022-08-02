package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Import({CidadeService.class})
@Transactional
@Sql(scripts = {"classpath:/tests_cidade.sql"})
public class CidadeServiceTest {

    @Autowired
    private CidadeService service;
    @MockBean
    private AutenticacaoService autenticacaoService;

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

    @Test
    public void findByEstadoNomeAndCidadeNome_deveRetornarException_quandoNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findFirstByUfNomeAndCidadeNome("LONDRINA", "PI"))
            .withMessage("Cidade não encontrada.");
    }

    @Test
    public void findByEstadoNomeAndCidadeNome_deveRetornarApenasPrimeiraCidade_quandoExistirDuasOuMais() {
        assertThat(service.findFirstByUfNomeAndCidadeNome("SP", "SAO PAULO"))
            .extracting("id", "nome")
            .containsExactly(6578, "SAO PAULO");
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaVaziaDeCidadesId() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of()))
            .hasSize(0);
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaComCidadeIdNaoExistente() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(123123, 213213)))
            .hasSize(0);
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadeId() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of(5578, 3426, 4498)))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome")
            .containsExactlyInAnyOrder(
                tuple(5578, "LONDRINA", "4113700", 3, "SUL"),
                tuple(3426, "MARINGA", "4115200", 3, "SUL"),
                tuple(4498, "CHAPECO", "4204202", 3, "SUL"));
    }
}
