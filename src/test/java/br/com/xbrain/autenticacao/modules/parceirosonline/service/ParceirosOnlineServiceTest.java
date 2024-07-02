package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParceirosOnlineServiceTest {

    @InjectMocks
    private ParceirosOnlineService service;
    @Mock
    private ParceirosOnlineClient client;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void getClusters_deveRetornarListaClusterDto_quandoUsuarioConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        when(client.getClusters(1))
            .thenReturn(List.of(umClusterDto()));

        assertThat(service.getClusters(1))
            .extracting("id", "nome", "grupo.id", "grupo.nome", "situacao")
            .containsExactly(tuple(1, "CLUSTER", 1, "GRUPO", ESituacao.A));

        verify(client).getClusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getClusters_deveRetornarListaVazia_quandoUsuarioNaoConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.D2D_PROPRIO));

        assertThat(service.getClusters(1))
            .isEqualTo(List.of());

        verify(client, never()).getClusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getClusters_deveLancarIntegracaoException_quandoApiIndisponivel() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(RetryableException.class)
            .when(client)
            .getClusters(1);

        assertThatThrownBy(() -> service.getClusters(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#018 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getClusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getClusters_deveLancarIntegracaoException_quandoErroNaApi() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getClusters(1);

        assertThatThrownBy(() -> service.getClusters(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getClusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getGrupos_deveRetornarListaGrupoDto_quandoUsuarioConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        when(client.getGrupos(1))
            .thenReturn(List.of(umGrupoDto()));

        assertThat(service.getGrupos(1))
            .extracting("id", "nome", "regional.id", "regional.nome", "situacao")
            .containsExactly(tuple(1, "GRUPO", 1, "REGIONAL", ESituacao.A));

        verify(client).getGrupos(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getGrupos_deveRetornarListaVazia_quandoUsuarioNaoConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.D2D_PROPRIO));

        assertThat(service.getGrupos(1))
            .isEqualTo(List.of());

        verify(client, never()).getGrupos(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getGrupos_deveLancarIntegracaoException_quandoApiIndisponivel() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(RetryableException.class)
            .when(client)
            .getGrupos(1);

        assertThatThrownBy(() -> service.getGrupos(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#018 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getGrupos(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getGrupos_deveLancarIntegracaoException_quandoErroNaApi() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getGrupos(1);

        assertThatThrownBy(() -> service.getGrupos(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getGrupos(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getRegionais_deveRetornarListaRegionaisDto_quandoUsuarioConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        when(client.getRegionais())
            .thenReturn(List.of(umaRegionalDto()));

        assertThat(service.getRegionais())
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(1, "REGIONAL", ESituacao.A));

        verify(client).getRegionais();
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getRegionais_deveRetornarListaVazia_quandoUsuarioNaoConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.D2D_PROPRIO));

        assertThat(service.getRegionais())
            .isEqualTo(List.of());

        verify(client, never()).getRegionais();
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getRegionais_deveLancarIntegracaoException_quandoApiIndisponivel() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(RetryableException.class)
            .when(client)
            .getRegionais();

        assertThatThrownBy(() -> service.getRegionais())
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#018 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getRegionais();
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getRegionais_deveLancarIntegracaoException_quandoErroNaApi() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getRegionais();

        assertThatThrownBy(() -> service.getRegionais())
            .isInstanceOf(IntegracaoException.class);

        verify(client).getRegionais();
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getSubclusters_deveRetornarListaGrupoDto_quandoUsuarioConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        when(client.getSubclusters(1))
            .thenReturn(List.of(umSubClusterDto()));

        assertThat(service.getSubclusters(1))
            .extracting("id", "nome", "cluster.id", "cluster.nome", "situacao")
            .containsExactly(tuple(1, "SUB CLUSTER", 1, "CLUSTER", ESituacao.A));

        verify(client).getSubclusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getSubclusters_deveRetornarListaVazia_quandoUsuarioNaoConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.D2D_PROPRIO));

        assertThat(service.getSubclusters(1))
            .isEqualTo(List.of());

        verify(client, never()).getSubclusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getSubclusters_deveLancarIntegracaoException_quandoApiIndisponivel() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(RetryableException.class)
            .when(client)
            .getSubclusters(1);

        assertThatThrownBy(() -> service.getSubclusters(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#018 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getSubclusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getSubclusters_deveLancarIntegracaoException_quandoErroNaApi() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getSubclusters(1);

        assertThatThrownBy(() -> service.getSubclusters(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getSubclusters(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getCidades_deveRetornarListaGrupoDto_quandoUsuarioConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        when(client.getCidades(1))
            .thenReturn(List.of(umUsuarioCidadeDto()));

        assertThat(service.getCidades(1))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional",
                "fkCidade", "cidadePai")
            .containsExactly(tuple(1, "LONDRINA", 2, "PARANA", 3, "REGIONAL", 1, "LONDRINA"));

        verify(client).getCidades(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getCidades_deveRetornarListaVazia_quandoUsuarioNaoConterCanalAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.D2D_PROPRIO));

        assertThat(service.getCidades(1))
            .isEqualTo(List.of());

        verify(client, never()).getCidades(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getCidades_deveLancarIntegracaoException_quandoApiIndisponivel() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(RetryableException.class)
            .when(client)
            .getCidades(1);

        assertThatThrownBy(() -> service.getCidades(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#018 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getCidades(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void getCidades_deveLancarIntegracaoException_quandoErroNaApi() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(ECanal.AGENTE_AUTORIZADO));

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getCidades(1);

        assertThatThrownBy(() -> service.getCidades(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getCidades(1);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    private ClusterDto umClusterDto() {
        return ClusterDto.builder()
            .id(1)
            .nome("CLUSTER")
            .grupo(umGrupoDto())
            .situacao(ESituacao.A)
            .build();
    }

    private GrupoDto umGrupoDto() {
        return GrupoDto.builder()
            .id(1)
            .nome("GRUPO")
            .regional(umaRegionalDto())
            .situacao(ESituacao.A)
            .build();
    }

    private RegionalDto umaRegionalDto() {
        return RegionalDto.builder()
            .id(1)
            .nome("REGIONAL")
            .situacao(ESituacao.A)
            .build();
    }

    private SubClusterDto umSubClusterDto() {
        return SubClusterDto.builder()
            .id(1)
            .nome("SUB CLUSTER")
            .situacao(ESituacao.A)
            .cluster(umClusterDto())
            .build();
    }

    private UsuarioCidadeDto umUsuarioCidadeDto() {
        return UsuarioCidadeDto.builder()
            .idCidade(1)
            .nomeCidade("LONDRINA")
            .idUf(2)
            .nomeUf("PARANA")
            .idRegional(3)
            .nomeRegional("REGIONAL")
            .fkCidade(1)
            .cidadePai("LONDRINA")
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(ECanal canal) {
        return UsuarioAutenticado.builder()
            .id(101)
            .nome("OPERADOR TELEVENDAS")
            .nivelCodigo("OPERACAO")
            .canais(Set.of(canal))
            .usuario(Usuario.builder()
                .id(1)
                .cpf("097.238.645-92")
                .nome("SEIVA")
                .situacao(ESituacao.A)
                .canais(Set.of(canal)).build())
            .cargoCodigo(CodigoCargo.OPERACAO_TELEVENDAS)
            .build();
    }
}
