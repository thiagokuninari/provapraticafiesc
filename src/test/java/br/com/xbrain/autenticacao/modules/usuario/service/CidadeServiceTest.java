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
import static org.mockito.Mockito.*;

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
        when(cidadeRepository.findAllByRegionalIdAndUfId(anyInt(), anyInt(), any(Predicate.class)))
            .thenReturn(List.of(Cidade.builder().id(5578).nome("LONDRINA")
                .uf(Uf.builder().id(1).nome("PARANA").build())
                .regional(Regional.builder().id(1027).nome("RPS").build()).build()));
        assertThat(service.getAllCidadeByRegionalAndUf(1027, 1))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome")
            .contains(tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"));
    }

    @Test
    public void getAllByRegionalId_deveRetornarCidades_quandoExistir() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1).build());
        when(cidadeRepository.findAllByRegionalId(anyInt(), any(Predicate.class)))
            .thenReturn(List.of(
                Cidade.builder().id(5578).nome("LONDRINA")
                    .uf(Uf.builder().id(1).nome("PARANA").build())
                    .regional(Regional.builder().id(1001).nome("RS").build()).build(),
                Cidade.builder().id(4519).nome("FLORIANOPOLIS")
                    .uf(Uf.builder().id(22).nome("SANTA CATARINA").build())
                    .regional(Regional.builder().id(1001).nome("RS").build()).build()
            ));
        assertThat(service.getAllByRegionalId(1001))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(
                tuple(5578, "LONDRINA", 1, "PARANA", 1001, "RS"),
                tuple(4519, "FLORIANOPOLIS", 22, "SANTA CATARINA", 1001, "RS"));
    }

    @Test
    public void getAllByRegionalId_deveRetornarCidades_quandoInformarNovaRegional() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1).build());
        when(cidadeRepository.findAllByNovaRegionalId(anyInt(), any(Predicate.class)))
            .thenReturn(List.of(
                Cidade.builder().id(5578).nome("LONDRINA")
                    .uf(Uf.builder().id(1).nome("PARANA").build())
                    .regional(Regional.builder().id(1027).nome("RPS").build()).build(),
                Cidade.builder().id(4519).nome("FLORIANOPOLIS")
                    .uf(Uf.builder().id(22).nome("SANTA CATARINA").build())
                    .regional(Regional.builder().id(1027).nome("RPS").build()).build()
            ));
        assertThat(service.getAllByRegionalId(1027))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(
                tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"),
                tuple(4519, "FLORIANOPOLIS", 22, "SANTA CATARINA", 1027, "RPS"));
    }

    @Test
    public void buscarTodas_deveBuscarTodasPorRegionalEUf_quandoPassarUfIdERegionalId() {
        service.buscarTodas(1, 2, null);

        verify(cidadeRepository).findAllByRegionalIdAndUfId(eq(2), eq(1), any());
        verify(cidadeRepository, never()).findCidadeByUfId(anyInt(), any());
        verify(cidadeRepository, never()).findBySubCluster(anyInt());
    }

    @Test
    public void buscarTodas_deveBuscarTodasPorRegionalEUf_quandoPassarUfId() {
        service.buscarTodas(1, null, null);

        verify(cidadeRepository).findCidadeByUfId(eq(1), any());
        verify(cidadeRepository, never()).findAllByRegionalIdAndUfId(anyInt(), anyInt(), any());
        verify(cidadeRepository, never()).findBySubCluster(anyInt());
    }

    @Test
    public void buscarTodas_deveBuscarTodasPorRegionalEUf_quandoPassarSubCluesterId() {
        service.buscarTodas(null, null, 3);

        verify(cidadeRepository).findBySubCluster(3);
        verify(cidadeRepository, never()).findCidadeByUfId(anyInt(), any());
        verify(cidadeRepository, never()).findAllByRegionalIdAndUfId(anyInt(), anyInt(), any());
    }
}
