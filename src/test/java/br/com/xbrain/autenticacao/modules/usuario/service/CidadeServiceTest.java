package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoCidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.types.Predicate;
import org.assertj.core.groups.Tuple;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.service.CidadeService.hasFkCidadeSemNomeCidadePai;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

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
    @Mock
    private CidadeService self;

    @Test
    public void buscarTodas_deveRetornarTodasAsCidadesComRegionalEComUf_quandoUfIdERegionalIdNaoNullos() {
        when(cidadeRepository.findAllByRegionalIdAndUfId(1, 2, new CidadePredicate().build()))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.buscarTodas(2, 1, 4))
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome")
            .containsExactly(Tuple.tuple(5578, "LONDRINA", "1234", 1, "PARANA"));

        verify(cidadeRepository).findAllByRegionalIdAndUfId(1, 2, new CidadePredicate().build());
        verify(cidadeRepository, never()).findCidadeByUfId(anyInt(), any(Sort.class));
        verify(cidadeRepository, never()).findBySubCluster(anyInt());
    }

    @Test
    public void buscarTodas_deveRetornarTodasAsCidadesComUf_quandoUfIdNaoNull() {
        when(cidadeRepository.findCidadeByUfId(2, new Sort("nome")))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.buscarTodas(2, null, 4))
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome")
            .containsExactly(Tuple.tuple(5578, "LONDRINA", "1234", 1, "PARANA"));

        verify(cidadeRepository).findCidadeByUfId(2, new Sort("nome"));
        verify(cidadeRepository, never()).findAllByRegionalIdAndUfId(anyInt(), anyInt(), any(Predicate.class));
        verify(cidadeRepository, never()).findBySubCluster(anyInt());
    }

    @Test
    public void buscarTodas_deveRetornarTodasAsCidadesComSubCluster_quandoUfIdNull() {
        when(cidadeRepository.findBySubCluster(4))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.buscarTodas(null, 1, 4))
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome")
            .containsExactly(Tuple.tuple(5578, "LONDRINA", "1234", 1, "PARANA"));

        verify(cidadeRepository).findBySubCluster(4);
        verify(cidadeRepository, never()).findCidadeByUfId(anyInt(), any(Sort.class));
        verify(cidadeRepository, never()).findAllByRegionalIdAndUfId(anyInt(), anyInt(), any(Predicate.class));
    }

    @Test
    public void buscarTodas_deveRetornarListaVazia_quandoUfIdESubClusterIdNullos() {
        assertThat(service.buscarTodas(null, 1, null))
            .isEmpty();

        verify(cidadeRepository, never()).findBySubCluster(anyInt());
        verify(cidadeRepository, never()).findCidadeByUfId(anyInt(), any(Sort.class));
        verify(cidadeRepository, never()).findAllByRegionalIdAndUfId(anyInt(), anyInt(), any(Predicate.class));
    }

    @Test
    public void getCidadeByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() {
        when(cidadeRepository.findCidadeComSite(any(Predicate.class)))
            .thenReturn(Optional.of(CidadeSiteResponse.builder()
                .id(5578).nome("LONDRINA").uf("PR").siteId(100).build()));

        assertThat(service.getCidadeByCodigoCidadeDbm(3))
            .extracting("id", "siteId", "nome", "uf")
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
            .extracting("id", "siteId", "nome", "uf")
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
                    .regional(Regional.builder().id(1027).nome("RPS").build()).build(),
                Cidade.builder().id(4519).nome("FLORIANOPOLIS")
                    .uf(Uf.builder().id(22).nome("SANTA CATARINA").build())
                    .regional(Regional.builder().id(1027).nome("RPS").build()).build()));
        assertThat(service.getAllByRegionalId(1027))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(
                tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"),
                tuple(4519, "FLORIANOPOLIS", 22, "SANTA CATARINA", 1027, "RPS"));
    }

    @Test
    public void getAllByRegionalIdAndUfId_deveRetornarCidades_quandoRegionalIdEUfIdForemInformados() {
        var usuarioAutenticado = UsuarioAutenticado.builder().id(1).build();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        var predicate = new CidadePredicate().filtrarPermitidos(usuarioAutenticado).build();

        when(cidadeRepository.findAllByRegionalIdAndUfId(eq(1027), eq(1), eq(predicate)))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getAllByRegionalIdAndUfId(1027, 1))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(cidadeRepository).findAllByRegionalIdAndUfId(eq(1027), eq(1), eq(predicate));

    }

    @Test
    public void getCidadesByRegionalReprocessamento_deveRetornarUsuariosCidadeDto_quandoRegionalIdForInformado() {
        var predicate = new CidadePredicate().build();

        when(cidadeRepository.findAllByRegionalId(eq(1027), eq(predicate)))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getCidadesByRegionalReprocessamento(1027))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"));

        verify(cidadeRepository).findAllByRegionalId(eq(1027), eq(predicate));
    }

    @Test
    public void getCidadesByRegionalAndUfReprocessamento_deveRetornarUsuariosCidadeDto_seRegionalIdEUfIdForemInformados() {
        var predicate = new CidadePredicate().build();

        when(cidadeRepository.findAllByRegionalIdAndUfId(eq(1027), eq(1), eq(predicate)))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getCidadesByRegionalAndUfReprocessamento(1027, 1))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"));

        verify(cidadeRepository).findAllByRegionalIdAndUfId(eq(1027), eq(1), eq(predicate));
    }

    @Test
    public void getAllBySubClusterId_deveRetornarUsuariosCidadeDto_quandoSubClusterIdForInformado() {
        var usuarioAutenticado = UsuarioAutenticado.builder().id(1).build();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        var predicate = new CidadePredicate().filtrarPermitidos(usuarioAutenticado).build();

        when(cidadeRepository.findAllBySubClusterId(eq(1), eq(predicate)))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getAllBySubClusterId(1))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional")
            .contains(tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(cidadeRepository).findAllBySubClusterId(1, predicate);
    }

    @Test
    public void getAllBySubCluster_deveRetornarCidades_quandoSubClusterIdForInformado() {
        when(cidadeRepository.findBySubCluster(eq(3)))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getAllBySubCluster(3))
            .extracting("id", "nome", "uf.id", "regional.id")
            .contains(tuple(5578, "LONDRINA", 1, 1027));

        verify(cidadeRepository).findBySubCluster(eq(3));
    }

    @Test
    public void findByUfNomeAndCidadeNome_deveRetornarCidades_quandoCidadeEncontrada() {
        var predicate = new CidadePredicate().comNome("LONDRINA").comUf("PARANA");
        when(cidadeRepository.findByPredicate(predicate.build()))
            .thenReturn(Optional.ofNullable(umaCidade()));

        assertThat(service.findByUfNomeAndCidadeNome("PARANA", "LONDRINA"))
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome")
            .containsExactly(5578, "LONDRINA", "1234", 1, "PARANA");

        verify(cidadeRepository).findByPredicate(predicate.build());
    }

    @Test
    public void findByUfNomeAndCidadeNome_deveRetornarException_quandoCidadeNaoEncontrada() {
        assertThatCode( () -> service.findByUfNomeAndCidadeNome("GOIAS", "GOIANA"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Cidade não encontrada.");

        verify(cidadeRepository).findByPredicate(new CidadePredicate().comNome("GOIANA").comUf("GOIAS").build());
    }

    @Test
    public void getClusterizacao_deveRetornarClusterizacaoDto_quandoCidadeIdForInformado() {
        var clusterizacaoDto = new ClusterizacaoDto();
        clusterizacaoDto.setCidadeId(5);
        clusterizacaoDto.setCidadeNome("LONDRINA");

        when(cidadeRepository.getClusterizacao(eq(5)))
            .thenReturn(clusterizacaoDto);

        assertThat(service.getClusterizacao(5))
            .extracting("cidadeId", "cidadeNome")
            .contains(5, "LONDRINA");

        verify(cidadeRepository).getClusterizacao(eq(5));
    }

    @Test
    public void findCidadeByCodigoIbge_deveRetornarCidadeResponse_quandoCodigoIbgeForInformado() {
        when(cidadeRepository.findCidadeByCodigoIbge(eq("1234")))
            .thenReturn(Optional.of(
                umaCidade()));

        assertThat(service.findCidadeByCodigoIbge("1234"))
            .extracting("id", "nome", "uf.id", "regional.id", "codigoIbge")
            .contains(5578, "LONDRINA", 1, 1027, "1234");

        verify(cidadeRepository).findCidadeByCodigoIbge(eq("1234"));
    }

    @Test
    public void findCidadesByCodigosIbge_deveRetornarListaCidadeResponse_quandoListaCodigoIbgeForInformado() {
        var predicate = new CidadePredicate().comCodigosIbge(List.of("IBGE"));
        when(cidadeRepository.findCidadesByCodigosIbge(predicate.build()))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.findCidadesByCodigosIbge(List.of("IBGE")))
            .extracting("id", "nome", "uf.id", "regional.id", "codigoIbge")
            .contains(Tuple.tuple(5578, "LONDRINA", 1, 1027, "1234"));

        verify(cidadeRepository).findCidadesByCodigosIbge(predicate.build());
    }

    @Test
    public void findCidadesByCodigosIbge_deveRetornarListaVazia_quandoCidadesNaoEncontrado() {
        var predicate = new CidadePredicate().comCodigosIbge(List.of("XUXU"));
        when(cidadeRepository.findCidadesByCodigosIbge(predicate.build()))
            .thenReturn(List.of());

        assertThat(service.findCidadesByCodigosIbge(List.of("XUXU")))
            .isEmpty();

        verify(cidadeRepository).findCidadesByCodigosIbge(predicate.build());
    }

    @Test
    public void findById_deveRetornarCidade_quandoExistirCidade() {
        when(cidadeRepository.findOne(eq(1)))
            .thenReturn(umaCidade());

        assertThat(service.findById(1))
            .extracting("id", "nome", "uf.id", "regional.id", "codigoIbge")
            .contains(5578, "LONDRINA", 1, 1027, "1234");

        verify(cidadeRepository).findOne(eq(1));
    }

    @Test
    public void getAllCidadeNetUno_deveRetornarListaCidadeResponse_quandoCidadeEncontrada() {
        when(cidadeRepository.findAllByNetUno(Eboolean.V))
            .thenReturn(List.of(umaCidade()));

        assertThat(service.getAllCidadeNetUno())
            .extracting("id", "nome", "uf.id", "regional.id", "codigoIbge")
            .contains(Tuple.tuple(5578, "LONDRINA", 1, 1027, "1234"));

        verify(cidadeRepository).findAllByNetUno(Eboolean.V);
    }

    @Test
    public void getAllCidadeNetUno_deveRetornarListaVazia_quandoCidadeNaoEncontrada() {
        when(cidadeRepository.findAllByNetUno(Eboolean.V))
            .thenReturn(List.of());

        assertThat(service.getAllCidadeNetUno())
            .isEmpty();

        verify(cidadeRepository).findAllByNetUno(Eboolean.V);
    }

    @Test
    public void getCidadeDistrito_deveRetornarCidade_quandoExistir() {
        var cidadeDistrito = Cidade.builder().id(30848).nome("SAO LUIZ").fkCidade(5578)
            .uf(Uf.builder().id(1).nome("PARANA").uf("PR").build())
            .regional(Regional.builder().id(1027).nome("RS").build()).build();
        when(cidadeRepository.buscarCidadeDistrito(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(cidadeDistrito));
        assertThat(service.getCidadeDistrito("PR", "LONDRINA", "SAO LUIZ"))
            .extracting("id", "nome", "uf.id", "uf.nome")
            .containsExactly(30848, "SAO LUIZ", 1, "PARANA");
    }

    @Test
    public void getCidadeDistrito_deveLancarException_quandoNaoExistirCidadeDistrito() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getCidadeDistrito("PR", "LONDRINA", "TESTE"))
            .withMessage("Cidade não encontrada.");
    }

    @Test
    public void getAll_deveRetornarListaCidadeResponseCompletaDasCidadesComDistritos_quandoInformarParametrosNull() {
        var booleanBuilder = new CidadePredicate().build();

        when(cidadeRepository.findAllByPredicate(booleanBuilder))
            .thenReturn(umaListaComCidadesEDistritos());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getAll(null, null)).hasSize(30);

        verify(cidadeRepository).findAllByPredicate(booleanBuilder);
        verifyZeroInteractions(autenticacaoService);
    }

    @Test
    public void getAll_deveRetornarListaVazia_quandoNaoExistirPorRegionalId() {
        var booleanBuilder = new CidadePredicate().comRegionalId(500).build();

        assertThat(service.getAll(500, null)).isEmpty();

        verify(cidadeRepository).findAllByPredicate(booleanBuilder);
        verifyZeroInteractions(self);
    }

    @Test
    public void getAll_deveRetornarListaVazia_quandoNaoExistirPorUfId() {
        var booleanBuilder = new CidadePredicate().comUfId(50).build();

        assertThat(service.getAll(null, 50)).isEmpty();

        verify(cidadeRepository).findAllByPredicate(booleanBuilder);
        verifyZeroInteractions(self);
    }

    @Test
    public void getAll_deveRetornarListaVazia_quandoNaoExistirPorRegionalIdComUfId() {
        var booleanBuilder = new CidadePredicate().comRegionalId(500).comUfId(50).build();

        assertThat(service.getAll(500, 50)).isEmpty();

        verify(cidadeRepository).findAllByPredicate(booleanBuilder);
        verifyZeroInteractions(self);
    }

    @Test
    public void getAll_deveRetornarListaCidadeResponse_quandoInformarApenasRegionalId() {
        var predicate = new CidadePredicate().comRegionalId(1027).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(listaCidadesDoParanaEDistritosDeLondrina());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getAll(1027, null))
            .hasSize(14)
            .extracting("id", "nome", "regional.id", "regional.nome")
            .containsExactly(
                tuple(3248, "BANDEIRANTES", 1027, "RPS"),
                tuple(3270, "CAMBE", 1027, "RPS"),
                tuple(3272, "CAMPINA DA LAGOA", 1027, "RPS"),
                tuple(3287, "CASCAVEL", 1027, "RPS"),
                tuple(3312, "CURITIBA", 1027, "RPS"),
                tuple(30858, "GUARAVERA", 1027, "RPS"),
                tuple(30813, "IRERE", 1027, "RPS"),
                tuple(30732, "LERROVILLE", 1027, "RPS"),
                tuple(5578, "LONDRINA", 1027, "RPS"),
                tuple(30757, "MARAVILHA", 1027, "RPS"),
                tuple(3426, "MARINGA", 1027, "RPS"),
                tuple(30676, "PAIQUERE", 1027, "RPS"),
                tuple(30848, "SAO LUIZ", 1027, "RPS"),
                tuple(30910, "WARTA", 1027, "RPS")
            );

        verify(cidadeRepository).findAllByPredicate(predicate);
    }

    @Test
    public void getAll_deveRetornarListaCidadeResponse_quandoInformarApenasUfId() {
        var predicate = new CidadePredicate().comUfId(2).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(listaCidadesDeSaoPaulo());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getAll(null, 2))
            .hasSize(12)
            .extracting("id", "nome", "uf.id", "uf.uf")
            .containsExactly(
                tuple(33618, "ALDEIA", 2, "SP"),
                tuple(4864, "BARUERI", 2, "SP"),
                tuple(4870, "BERNARDINO DE CAMPOS", 2, "SP"),
                tuple(4903, "CAJAMAR", 2, "SP"),
                tuple(4943, "COSMOPOLIS", 2, "SP"),
                tuple(4944, "COSMORAMA", 2, "SP"),
                tuple(33252, "JARDIM BELVAL", 2, "SP"),
                tuple(33255, "JARDIM SILVEIRA", 2, "SP"),
                tuple(33269, "JORDANESIA", 2, "SP"),
                tuple(5107, "LINS", 2, "SP"),
                tuple(5128, "MARILIA", 2, "SP"),
                tuple(33302, "POLVILHO", 2, "SP")
            );

        verify(cidadeRepository).findAllByPredicate(predicate);
    }

    @Test
    public void getAll_deveRetornarListaCidadeResponse_quandoInformarRegionalIdUfIdECidadePaiEstiverNaMesmaLista() {
        var predicate = new CidadePredicate().comRegionalId(1027).comUfId(1).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(listaCidadesDoParanaEDistritosDeLondrina());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getAll(1027, 1))
            .hasSize(14)
            .extracting("id", "nome", "uf.id", "uf.uf", "regional.id", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(3248, "BANDEIRANTES", 1, "PR", 1027, null, null),
                tuple(3270, "CAMBE", 1, "PR", 1027, null, null),
                tuple(3272, "CAMPINA DA LAGOA", 1, "PR", 1027, null, null),
                tuple(3287, "CASCAVEL", 1, "PR", 1027, null, null),
                tuple(3312, "CURITIBA", 1, "PR", 1027, null, null),
                tuple(30858, "GUARAVERA", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(30813, "IRERE", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(30732, "LERROVILLE", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(5578, "LONDRINA", 1, "PR", 1027, null, null),
                tuple(30757, "MARAVILHA", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(3426, "MARINGA", 1, "PR", 1027, null, null),
                tuple(30676, "PAIQUERE", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(30848, "SAO LUIZ", 1, "PR", 1027, 5578, "LONDRINA"),
                tuple(30910, "WARTA", 1, "PR", 1027, 5578, "LONDRINA")
            );

        verify(cidadeRepository).findAllByPredicate(predicate);
    }

    @Test
    public void getAll_deveRetornarListaCidadeResponse_quandoInformarApenasRegionalIdUfIdECidadePaiNaoEstiverNaMesmaLista() {
        var predicate = new CidadePredicate().comRegionalId(1031).comUfId(2).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(listaCidadesComUfSaoPauloERegionalSci());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getAll(1031, 2))
            .hasSize(10)
            .extracting("id", "nome", "uf.id", "uf.uf", "regional.id", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(33618, "ALDEIA", 2, "SP", 1031, 4864, "BARUERI"),
                tuple(4870, "BERNARDINO DE CAMPOS", 2, "SP", 1031, null, null),
                tuple(4943, "COSMOPOLIS", 2, "SP", 1031, null, null),
                tuple(4944, "COSMORAMA", 2, "SP", 1031, null, null),
                tuple(33252, "JARDIM BELVAL", 2, "SP", 1031, 4864, "BARUERI"),
                tuple(33255, "JARDIM SILVEIRA", 2, "SP", 1031, 4864, "BARUERI"),
                tuple(33269, "JORDANESIA", 2, "SP", 1031, 4903, "CAJAMAR"),
                tuple(5107, "LINS", 2, "SP", 1031, null, null),
                tuple(5128, "MARILIA", 2, "SP", 1031, null, null),
                tuple(33302, "POLVILHO", 2, "SP", 1031, 4903, "CAJAMAR")
            );

        verify(cidadeRepository).findAllByPredicate(predicate);
    }

    @Test
    public void buscarCidadesPorEstadosIds_deveRetornarListaVazia_quandoListaEstadosIdsForVazia() {
        assertThat(service.buscarCidadesPorEstadosIds(List.of())).isEmpty();

        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void buscarCidadesPorEstadosIds_deveRetornarListaVazia_quandoNaoExistirPorEstadosIds() {
        var estadosIds = List.of(50, 51);

        assertThat(service.buscarCidadesPorEstadosIds(estadosIds)).isEmpty();

        verify(cidadeRepository).findAllByUfIdInOrderByNome(estadosIds);
        verifyZeroInteractions(self);
    }

    @Test
    public void buscarCidadesPorEstadosIds_deveRetornarListaSelectResponse_quandoInformarListaEstadosIds() {
        var estadosIds = List.of(1);

        when(cidadeRepository.findAllByUfIdInOrderByNome(estadosIds))
            .thenReturn(listaCidadesDoParanaEDistritosDeLondrina());
        when(self.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.buscarCidadesPorEstadosIds(List.of(1)))
            .hasSize(14)
            .extracting("value", "label")
            .containsExactly(
                tuple(3248, "BANDEIRANTES - PR"),
                tuple(3270, "CAMBE - PR"),
                tuple(3272, "CAMPINA DA LAGOA - PR"),
                tuple(3287, "CASCAVEL - PR"),
                tuple(3312, "CURITIBA - PR"),
                tuple(30858, "GUARAVERA - LONDRINA - PR"),
                tuple(30813, "IRERE - LONDRINA - PR"),
                tuple(30732, "LERROVILLE - LONDRINA - PR"),
                tuple(5578, "LONDRINA - PR"),
                tuple(30757, "MARAVILHA - LONDRINA - PR"),
                tuple(3426, "MARINGA - PR"),
                tuple(30676, "PAIQUERE - LONDRINA - PR"),
                tuple(30848, "SAO LUIZ - LONDRINA - PR"),
                tuple(30910, "WARTA - LONDRINA - PR")
            );

        verify(cidadeRepository).findAllByUfIdInOrderByNome(estadosIds);
    }

    @Test
    public void hasFkCidadeSemNomeCidadePai_deveRetornarFalse_quandoFkCidadeForNull() {
        assertFalse(hasFkCidadeSemNomeCidadePai(null, null));
    }

    @Test
    public void hasFkCidadeSemNomeCidadePai_deveRetornarFalse_quandoNomeCidadePaiNaoForNull() {
        assertFalse(hasFkCidadeSemNomeCidadePai(5578, "LONDRINA"));
    }

    @Test
    public void hasFkCidadeSemNomeCidadePai_deveRetornarTrue_quandoFkCidadeNaoForNullENomeCidadePaiForNull() {
        assertTrue(hasFkCidadeSemNomeCidadePai(5578, null));
    }

    @Test
    public void getCidadeById_deveLancarValidacaoException_quandoNaoEncontrarCidadePorId() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getCidadeById(15000))
            .withMessage("Cidade não encontrada.");

        verify(cidadeRepository).findOne(15000);
    }

    @Test
    public void getCidadeById_deveRetornarCidadeResponseSemCidadePai_quandoEncontrarCidadePorId() {
        when(cidadeRepository.findOne(5578)).thenReturn(cidadeLondrina());

        assertThat(service.getCidadeById(5578))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", 1, "PARANA", 1027, "RPS", null, null);

        verify(cidadeRepository).findOne(5578);
    }

    @Test
    public void getCidadeById_deveRetornarCidadeResponseComCidadePai_quandoEncontrarCidadePorId() {
        when(cidadeRepository.findOne(30910)).thenReturn(distritoWarta());
        when(cidadeRepository.findOne(5578)).thenReturn(cidadeLondrina());

        assertThat(service.getCidadeById(30910))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");

        verify(cidadeRepository).findOne(30910);
        verify(cidadeRepository).findOne(5578);
    }

    @Test
    public void getAllCidadeByUfs_deveRetornarListaVazia_quandoListaUfIdsForVazia() {
        assertThat(service.getAllCidadeByUfs(List.of())).isEmpty();

        verify(cidadeRepository, never()).findAllByUfIdInOrderByNome(anyList());
    }

    @Test
    public void getAllCidadeByUfs_deveRetornarListaCidadeUfResponse_quandoInformarListaUfIds() {
        var estadosIds = List.of(2);

        when(cidadeRepository.findAllByUfIdInOrderByNome(estadosIds))
            .thenReturn(listaCidadesDeSaoPaulo());

        assertThat(service.getAllCidadeByUfs(estadosIds))
            .hasSize(12)
            .extracting("cidadeId", "cidade", "uf", "ufSigla", "ufId", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(33618, "ALDEIA", "SAO PAULO", "SP", 2, 4864, "BARUERI"),
                tuple(4870, "BERNARDINO DE CAMPOS", "SAO PAULO", "SP", 2, null, null),
                tuple(4943, "COSMOPOLIS", "SAO PAULO", "SP", 2, null, null),
                tuple(4944, "COSMORAMA", "SAO PAULO", "SP", 2, null, null),
                tuple(33252, "JARDIM BELVAL", "SAO PAULO", "SP", 2, 4864, "BARUERI"),
                tuple(33255, "JARDIM SILVEIRA", "SAO PAULO", "SP", 2, 4864, "BARUERI"),
                tuple(33269, "JORDANESIA", "SAO PAULO", "SP", 2, 4903, "CAJAMAR"),
                tuple(5107, "LINS", "SAO PAULO", "SP", 2, null, null),
                tuple(5128, "MARILIA", "SAO PAULO", "SP", 2, null, null),
                tuple(33302, "POLVILHO", "SAO PAULO", "SP", 2, 4903, "CAJAMAR"),
                tuple(4864, "BARUERI", "SAO PAULO", "SP", 2, null, null),
                tuple(4903, "CAJAMAR", "SAO PAULO", "SP", 2, null, null)
            );

        verify(cidadeRepository).findAllByUfIdInOrderByNome(estadosIds);
    }

    @Test
    public void getCidadesDistritos_deveRetornarMapApenasComDistritos_quandoInformarApenasDistritosComoV() {
        var predicate = new CidadePredicate().comDistritos(Eboolean.V).build();
        when(cidadeRepository.findAllByPredicate(predicate)).thenReturn(umaListaApenasDistritos());

        var predicateCidadesPai = new CidadePredicate().comCidadesId(umaListaApenasFkCidadeDosDistritos()).build();
        when(cidadeRepository.findAllByPredicate(predicateCidadesPai)).thenReturn(umaListaApenasCidades());

        assertThat(service.getCidadesDistritos(Eboolean.V)).hasSize(15);

        verify(cidadeRepository).findAllByPredicate(predicate);
        verify(cidadeRepository).findAllByPredicate(predicateCidadesPai);
        verifyNoMoreInteractions(cidadeRepository);
    }

    @Test
    public void getCidadesDistritos_deveRetornarMapApenasComCidades_quandoInformarApenasDistritosComoF() {
        var predicate = new CidadePredicate().comDistritos(Eboolean.F).build();
        when(cidadeRepository.findAllByPredicate(predicate)).thenReturn(umaListaApenasCidades());

        assertThat(service.getCidadesDistritos(Eboolean.F)).hasSize(15);

        verify(cidadeRepository).findAllByPredicate(predicate);
        verifyNoMoreInteractions(cidadeRepository);
    }

    @Test
    public void getCidadesDistritos_deveRetornarMapDeCidadesComDistritos_quandoInformarApenasDistritosComoNull() {
        var predicate = new CidadePredicate().comDistritos(null).build();
        var predicateCidadesPai = new CidadePredicate().comCidadesId(List.of(4864, 3272, 5578, 4903)).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(umaListaComCidadesEDistritos());
        when(cidadeRepository.findAllByPredicate(predicateCidadesPai))
            .thenReturn(List.of(cidadeCampinaDaLagoa(), cidadeBarueri(), cidadeCajamar(), cidadeLondrina()));

        assertThat(service.getCidadesDistritos(null))
            .hasSize(30);

        verify(cidadeRepository).findAllByPredicate(predicate);
        verify(cidadeRepository).findAllByPredicate(predicateCidadesPai);
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaVaziaDeCidadesId() {
        assertThat(service.getCodigoIbgeRegionalByCidade(List.of())).isEmpty();

        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaVazia_quandoInformarListaComCidadesIdNaoExistentes() {
        var cidadesIds = List.of(123123, 213213);
        var predicate = new CidadePredicate().comCidadesId(cidadesIds).build();

        when(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate)).thenReturn(List.of());

        assertThat(service.getCodigoIbgeRegionalByCidade(cidadesIds)).isEmpty();

        verify(cidadeRepository).findCodigoIbgeRegionalByCidade(predicate);
    }

    @Test
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadesId() {
        var cidadesIds = List.of(5578, 3426, 5107);
        var predicate = new CidadePredicate().comCidadesId(cidadesIds).build();

        when(cidadeRepository.findCodigoIbgeRegionalByCidade(predicate)).thenReturn(umaListaCodigoIbgeRegionalResponse());

        assertThat(service.getCodigoIbgeRegionalByCidade(cidadesIds))
            .hasSize(3)
            .extracting("cidadeId", "cidadeNome", "codigoIbge", "regionalId", "regionalNome")
            .containsExactlyInAnyOrder(
                tuple(5578, "LONDRINA", "4113700", 1027, "RPS"),
                tuple(3426, "MARINGA", "4115200", 1027, "RPS"),
                tuple(5107, "LINS", "3527108", 1031, "RSI")
            );

        verify(cidadeRepository).findCodigoIbgeRegionalByCidade(predicate);
    }

    private Cidade umaCidade() {
        return Cidade.builder()
            .id(5578)
            .nome("LONDRINA")
            .codigoIbge("1234")
            .uf(Uf.builder().id(1).nome("PARANA").build())
            .regional(Regional.builder().id(1027).nome("RPS").build())
            .build();
    }

    @Test
    public void getCidadesByCidadeInstalacaoIds_deveRetornarListaConfiguracaoCidade_quandoChamado() {
        var cidadesIds = List.of(5578, 3426, 5107);
        var predicate = new CidadePredicate().comCidadesId(cidadesIds).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(List.of(cidadeLondrina(), cidadeMaringa()));

        assertThat(service.getCidadesByCidadeInstalacaoIds(cidadesIds))
            .extracting(ConfiguracaoCidadeResponse::getId, ConfiguracaoCidadeResponse::getNome,
                ConfiguracaoCidadeResponse::getUf)
            .containsExactlyInAnyOrder(tuple(5578, "LONDRINA", "PR"), tuple(3426, "MARINGA", "PR"));

        verify(cidadeRepository).findAllByPredicate(predicate);
    }

    @Test
    public void getCidadesByCidadeInstalacaoIds_deveRetornarListaVazia_quandoCidadesNaoEncontradas() {
        var cidadesIds = List.of(123123, 213213);
        var predicate = new CidadePredicate().comCidadesId(cidadesIds).build();

        when(cidadeRepository.findAllByPredicate(predicate))
            .thenReturn(List.of());

        assertThat(service.getCidadesByCidadeInstalacaoIds(cidadesIds)).isEmpty();

        verify(cidadeRepository).findAllByPredicate(predicate);
    }
}
