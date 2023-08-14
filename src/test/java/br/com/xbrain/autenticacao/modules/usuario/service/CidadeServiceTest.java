package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.types.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CidadeServiceTest {

    @InjectMocks
    private CidadeService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private CidadeRepository cidadeRepository;
    @Mock
    private RegionalService regionalService;

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
    @Ignore
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
                    .regional(Regional.builder().id(1027).nome("RS").build()).build(),
                Cidade.builder().id(4519).nome("FLORIANOPOLIS")
                    .uf(Uf.builder().id(22).nome("SANTA CATARINA").build())
                    .regional(Regional.builder().id(1027).nome("RS").build()).build()
            ));
        assertThat(service.getAllByRegionalId(1027))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(
                tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RS"),
                tuple(4519, "FLORIANOPOLIS", 22, "SANTA CATARINA", 1027, "RS"));
    }
}
