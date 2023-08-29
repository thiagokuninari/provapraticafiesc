package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.GetLoginLogoutHojeRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.LoginLogoutHelper.umaListaLoginLogoutResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class NotificacaoUsuarioAcessoServiceTest {

    public static final String USUARIOS_IDS_REQUEST_PARAM = "usuariosIds";

    @InjectMocks
    private NotificacaoUsuarioAcessoService service;
    @Mock
    private NotificacaoUsuarioAcessoClient client;
    @Captor
    private ArgumentCaptor<Map<String, Object>> requestParamsArgCaptor;
    @Captor
    private ArgumentCaptor<GetLoginLogoutHojeRequest> getLoginLogoutHojeRequestArgCaptor;

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametrosComPageRequestEUsuariosIds_quandoPassarVariosUsuariosIds() {
        service.getLoginsLogoutsDeHoje(Optional.of(List.of(12, 1, 98)), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(getLoginLogoutHojeRequestArgCaptor.capture());

        assertThat(getLoginLogoutHojeRequestArgCaptor.getValue())
            .extracting("usuariosIds", "page", "size", "orderBy", "orderDirection")
            .containsExactly(Set.of(12, 1, 98), 6, 35, "nomeFantasia", "DESC");
    }

    @Test
    public void getLoginsLogoutsDeHoje_deveNaoPassarParametroUsuarioIds_quandoUsuarioIdsForNull() {
        service.getLoginsLogoutsDeHoje(Optional.empty(), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(getLoginLogoutHojeRequestArgCaptor.capture());

        assertThat(getLoginLogoutHojeRequestArgCaptor.getValue().getUsuariosIds()).isNull();
    }

    @Test
    public void getLoginsLogoutsDeHoje_deveNaoChamarOClientERetornarListaVazia_quandoUsuarioIdsForVazio() {
        assertThat(service.getLoginsLogoutsDeHoje(Optional.of(Set.of()), umPageRequest()))
            .extracting(MongoosePage::getDocs, MongoosePage::getTotalDocs)
            .containsExactly(List.of(), 0L);

        verify(client, never()).getLoginsLogoutsDeHoje(any());
    }

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametroUsuarioIdsUmIdVazia_quandoUsuarioIdsTiverApenasUmId() {
        service.getLoginsLogoutsDeHoje(Optional.of(Set.of(2002)), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(getLoginLogoutHojeRequestArgCaptor.capture());

        assertThat(getLoginLogoutHojeRequestArgCaptor.getValue().getUsuariosIds()).containsExactly(2002);
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveLancarIntegracaoException_quandoApiNaoAcessivel() {
        var request =  RelatorioLoginLogoutRequest.builder()
            .usuariosIds(List.of(1, 2, 3))
            .dataInicial(LocalDate.of(2021, 8, 5))
            .dataFinal(LocalDate.of(2021, 8, 5))
            .build();

        when(client.getLoginsLogoutsEntreDatas(eq(request)))
            .thenThrow(new RetryableException("Connection refused", new Date()));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarAcessosEntreDatasPorUsuarios(request))
            .withMessage("#037 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveLancarIntegracaoException_quandoClientRetornarErro() {
        var request =  RelatorioLoginLogoutRequest.builder()
            .usuariosIds(List.of(1, 2, 3))
            .dataInicial(LocalDate.of(2021, 8, 5))
            .dataFinal(LocalDate.of(2021, 8, 5))
            .build();

        when(client.getLoginsLogoutsEntreDatas(eq(request)))
            .thenThrow(new HystrixBadRequestException("Erro"));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarAcessosEntreDatasPorUsuarios(request))
            .withMessage("#037 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void buscarAcessosEntreDatasPorUsuarios_deveRetornarLista_seHouveremDados() {
        var request =  RelatorioLoginLogoutRequest.builder()
            .usuariosIds(IntStream.rangeClosed(1, 315).boxed().collect(Collectors.toList()))
            .dataInicial(LocalDate.of(2021, 8, 5))
            .dataFinal(LocalDate.of(2021, 8, 5))
            .build();

        when(client.getLoginsLogoutsEntreDatas(eq(request)))
            .thenReturn(umaListaLoginLogoutResponse());

        assertThat(service.buscarAcessosEntreDatasPorUsuarios(request))
            .isEqualTo(umaListaLoginLogoutResponse());

        verify(client, times(1))
            .getLoginsLogoutsEntreDatas(eq(request));
    }

    @Test
    public void getCsv_devePassarParametrosComFiltrosEUsuariosIdsDoFiltroMasTambemPermitidos_quandoApenasAlgunsIdsPermitidos() {
        var filtro = RelatorioLoginLogoutCsvFiltro.builder()
            .colaboradoresIds(Set.of(2002, 1998, 3000, 333, 1))
            .dataInicio(LocalDate.of(2015, 6, 25))
            .dataFim(LocalDate.of(2016, 1, 4))
            .build();
        service.getCsv(filtro, Optional.of(Set.of(500, 2002, 1, 10, 64, 1998)));

        verify(client, times(1)).getCsv(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).contains(
            entry("dataInicio", "25/06/2015"),
            entry("dataFim", "04/01/2016"),
            entry(USUARIOS_IDS_REQUEST_PARAM, "1,1998,2002")
        );
    }

    @Test
    public void getCsv_devePassarFiltroUsuariosIdsComTodosOsUsuariosIds_quandoIdsPermitidosForNull() {
        var filtro = RelatorioLoginLogoutCsvFiltro.builder()
            .colaboradoresIds(Set.of(2002, 1998, 3000, 333, 1))
            .dataInicio(LocalDate.of(2015, 6, 25))
            .dataFim(LocalDate.of(2016, 1, 4))
            .build();
        service.getCsv(filtro, Optional.empty());

        verify(client, times(1)).getCsv(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).containsEntry(USUARIOS_IDS_REQUEST_PARAM, "1,333,1998,2002,3000");
    }

    @Test
    public void getCsv_devePassarParametroUsuariosIdsComApenasUmId_quandoFiltroUsuariosIdsTiverApenasUmId() {
        var filtro = RelatorioLoginLogoutCsvFiltro.builder()
            .colaboradoresIds(Set.of(3000))
            .dataInicio(LocalDate.of(2015, 6, 25))
            .dataFim(LocalDate.of(2016, 1, 4))
            .build();
        service.getCsv(filtro, Optional.empty());

        verify(client, times(1)).getCsv(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).containsEntry(USUARIOS_IDS_REQUEST_PARAM, "3000");
    }

    @Test
    public void getCsv_deveNaoChamarOClientERetornarListaVazia_quandoNenhumUsuarioForPermitido() {
        var filtro = RelatorioLoginLogoutCsvFiltro.builder()
            .colaboradoresIds(Set.of(3000))
            .dataInicio(LocalDate.of(2015, 6, 25))
            .dataFim(LocalDate.of(2016, 1, 4))
            .build();

        assertThat(service.getCsv(filtro, Optional.of(List.of()))).isEmpty();

        verify(client, never()).getCsv(any());
    }

    @Test
    public void getUsuariosIdsByIds_deveRetornarOsIdsBuscandoPelosIds_quandoHouverIdsPassadosNoParametro() {
        when(client.getUsuariosIdsByIds(eq(List.of(14, 44, 1)))).thenReturn(List.of(44, 1));

        assertThat(service.getUsuariosIdsByIds(Optional.of(List.of(14, 44, 1))))
            .containsExactlyInAnyOrder(44, 1);

        verify(client, never()).getUsuariosIdsByIds(isNull());
    }

    @Test
    public void getUsuariosIdsByIds_deveRetornarOsIdsEBuscarPassandoPorParametroIdsNull_quandoUsuarioIdsForOptionalEmpty() {
        when(client.getUsuariosIdsByIds(isNull())).thenReturn(List.of(44, 1));

        assertThat(service.getUsuariosIdsByIds(Optional.empty()))
            .containsExactlyInAnyOrder(44, 1);
    }

    @Test
    public void getUsuariosIdsByIds_deveRetornarIdsVazioENaoChamarOClient_quandoUsuarioIdsForListaVazia() {
        assertThat(service.getUsuariosIdsByIds(Optional.of(List.of())))
            .isEmpty();

        verify(client, never()).getUsuariosIdsByIds(any());
    }

    @Test
    public void countUsuariosLogadosPorPeriodo_deveChamarClient_quandoChamado() {
        when(client.countUsuariosLogadosPorPeriodo(umUsuarioLogadoRequest())).thenReturn(umaListaUsuariosLogados());

        assertThat(service.countUsuariosLogadosPorPeriodo(umUsuarioLogadoRequest())).isEqualTo(umaListaUsuariosLogados());

        verify(client, times(1)).countUsuariosLogadosPorPeriodo(any(UsuarioLogadoRequest.class));
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveRetornarIdsRetornadoPeloClient_quandoHouverIdsPassadosNoParametro() {
        when(client.getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)))).thenReturn(List.of(44, 1));

        assertThat(service.getUsuariosLogadosAtualPorIds(List.of(14, 44, 1)))
            .containsExactlyInAnyOrder(44, 1);

        verify(client, times(1)).getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)));
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveRetornarListaVaziaENaoChamarClient_quandoUsuarioIdsForVazio() {
        assertThat(service.getUsuariosLogadosAtualPorIds(List.of()))
            .isEmpty();

        verify(client, never()).getUsuariosLogadosAtualPorIds(any());
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveRetornarListaVaziaENaoChamarClient_quandoUsuarioIdsForNulo() {
        assertThat(service.getUsuariosLogadosAtualPorIds(null))
            .isEmpty();

        verify(client, never()).getUsuariosLogadosAtualPorIds(any());
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveRetornarListaVazia_quandoClientRetornarListaVazia() {
        when(client.getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)))).thenReturn(List.of());

        assertThat(service.getUsuariosLogadosAtualPorIds(List.of(14, 44, 1)))
            .isEmpty();

        verify(client, times(1)).getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)));
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveLancarIntegracaoException_quandoApiNaoAcessivel() {
        when(client.getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1))))
            .thenThrow(new RetryableException("Connection refused", new Date()));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getUsuariosLogadosAtualPorIds(List.of(14, 44, 1)))
            .withMessage("#041 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client, times(1)).getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)));
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_deveLancarIntegracaoException_quandoClientRetornarErro() {
        when(client.getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1))))
            .thenThrow(new HystrixBadRequestException("Erro"));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getUsuariosLogadosAtualPorIds(List.of(14, 44, 1)))
            .withMessage("#041 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client, times(1)).getUsuariosLogadosAtualPorIds(eq(List.of(14, 44, 1)));
    }

    private UsuarioLogadoRequest umUsuarioLogadoRequest() {
        return UsuarioLogadoRequest.builder()
            .usuariosIds(List.of(101, 102, 103))
            .periodos(List.of(PaLogadoDto.builder()
                    .dataInicial("2020-12-01T10:00:00.000Z")
                    .dataFinal("2020-12-01T10:59:59.999Z")
                    .build(),
                PaLogadoDto.builder()
                    .dataInicial("2020-12-01T11:00:00.000Z")
                    .dataFinal("2020-12-01T11:42:39.999Z")
                    .build()))
            .build();
    }

    private List<PaLogadoDto> umaListaUsuariosLogados() {
        return List.of(
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T10:00:00.000Z")
                .dataFinal("2020-12-01T10:59:59.999Z")
                .totalUsuariosLogados(10)
                .build(),
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T11:00:00.000Z")
                .dataFinal("2020-12-01T11:42:39.999Z")
                .totalUsuariosLogados(3)
                .build());
    }

    @NotNull
    private PageRequest umPageRequest() {
        return new PageRequest(6, 35, "nomeFantasia", "DESC");
    }
}
