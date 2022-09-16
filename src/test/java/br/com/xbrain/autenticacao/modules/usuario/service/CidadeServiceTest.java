package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
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
    public void getCodigoIbgeRegionalByCidadeUf_deveRetornarListaVazia_quandoInformarListaVaziaDeCidades() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of())
            .ufs(List.of("PR", "PB", "RN"))
            .build();

        assertThat(service.getCodigoIbgeRegionalByCidadeNomeAndUf(listaCidadesUfs))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void getCodigoIbgeRegionalByCidadeUf_deveRetornarListaVazia_quandoInformarListaVaziaDeUfs() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of())
            .build();

        assertThat(service.getCodigoIbgeRegionalByCidadeNomeAndUf(listaCidadesUfs))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void getCodigoIbgeRegionalByCidadeUf_deveRetornarListaVazia_quandoValoresInexistentes() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("SP", "MG"))
            .build();

        assertThat(service.getCodigoIbgeRegionalByCidadeNomeAndUf(listaCidadesUfs))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void getCodigoIbgeRegionalByCidadeUf_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadeEUf() {
        var listaCidadesUfs = CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("PR", "PB", "RN"))
            .build();

        assertThat(service.getCodigoIbgeRegionalByCidadeNomeAndUf(listaCidadesUfs))
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome", "ufId", "estadoNome", "uf")
            .hasSize(3)
            .containsExactly(
                tuple(2641, "CARAUBAS", "2504074", 1, "LESTE", 24, "PARAIBA", "PB"),
                tuple(5578, "LONDRINA", "4113700", 3, "SUL", 1, "PARANA", "PR"),
                tuple(5604, "CARAUBAS", "2402303", 1, "LESTE", 26, "RIO GRANDE DO NORTE", "RN"));
    }
}
