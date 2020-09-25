package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.config.JacksonConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Before
    public void setup() {
        var objectMapper = new JacksonConfig().viewsObjectMapper();
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
    }

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametrosComPageRequestEUsuariosIds_quandoPassarVariosUsuariosIds() {
        service.getLoginsLogoutsDeHoje(Optional.of(List.of(12, 1, 98)), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).contains(
            entry("page", 6),
            entry("size", 35),
            entry("orderBy", "nomeFantasia"),
            entry("orderDirection", "DESC"),
            entry(USUARIOS_IDS_REQUEST_PARAM, "12,1,98")
        );
    }

    @Test
    public void getLoginsLogoutsDeHoje_deveNaoPassarParametroUsuarioIds_quandoUsuarioIdsForNull() {
        service.getLoginsLogoutsDeHoje(Optional.empty(), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).doesNotContainKey(USUARIOS_IDS_REQUEST_PARAM);
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

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).containsEntry(USUARIOS_IDS_REQUEST_PARAM, "2002");
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

    @NotNull
    private PageRequest umPageRequest() {
        return new PageRequest(6, 35, "nomeFantasia", "DESC");
    }
}
