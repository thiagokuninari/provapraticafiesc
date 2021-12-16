package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;

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
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    @MockBean
    private CidadeRepository cidadeRepository;

    @Test
    public void getCidadeByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() {
        when(cidadeRepository.findCidadeComSite(any(Predicate.class)))
            .thenReturn(Optional.of(CidadeSiteResponse.builder()
                .id(5578).nome("LONDRINA").uf("PR").siteId(100).build()));
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
        when(cidadeRepository.findCidadeComSite(any(Predicate.class)))
            .thenReturn(Optional.of(CidadeSiteResponse.builder()
                .id(5578).nome("LONDRINA").uf("PR").siteId(100).build()));
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
        when(cidadeRepository.findFirstByPredicate(any(Predicate.class)))
            .thenReturn(Optional.of(Cidade.builder().id(6578).nome("SAO PAULO").build()));
        assertThat(service.findFirstByUfNomeAndCidadeNome("SP", "SAO PAULO"))
            .extracting("id", "nome")
            .containsExactly(6578, "SAO PAULO");
    }

    @Test
    public void getAllCidadeByRegionalAndUf_deveRetornarCidades_quandoExistir() {
        when(cidadeRepository.findByRegionalIdAndUfId(anyInt(), anyInt()))
            .thenReturn(List.of(Cidade.builder().id(100).nome("LONDRINA")
                .uf(Uf.builder().id(20000).nome("PARANA").build())
                .regional(Regional.builder().id(100).nome("RPS").build()).build()));
        assertThat(service.getAllCidadeByRegionalAndUf(100, 20000))
            .extracting("id", "nome", "uf.nome", "regional.nome")
            .contains(tuple(100, "LONDRINA", "PARANA", "RPS"));
    }

    @Test
    public void getAllByRegionalId_deveRetornarCidades_quandoExistir() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1).build());
        when(cidadeRepository.findAllByRegionalId(anyInt(), any(Predicate.class)))
            .thenReturn(List.of(
                Cidade.builder().id(100).nome("LONDRINA")
                    .uf(Uf.builder().id(20000).nome("PARANA").build())
                    .regional(Regional.builder().id(100).nome("RPS").build()).build(),
                Cidade.builder().id(101).nome("BLUMENAU")
                    .uf(Uf.builder().id(30000).nome("SANTA CATARINA").build())
                    .regional(Regional.builder().id(100).nome("RPS").build()).build()
            ));
        assertThat(service.getAllByRegionalId(100))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(
                tuple(100, "LONDRINA", 20000, "PARANA", 100, "RPS"),
                tuple(101, "BLUMENAU", 30000, "SANTA CATARINA", 100, "RPS"));
    }
}
