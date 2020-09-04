package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.config.JacksonConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"unchecked", "rawtypes"})
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class NotificacaoUsuarioAcessoServiceTest {

    public static final String USUARIOS_IDS_REQUEST_PARAM = "usuariosIds";

    @InjectMocks
    private NotificacaoUsuarioAcessoService service;
    @Mock
    private NotificacaoUsuarioAcessoClient client;
    private ObjectMapper objectMapper = new JacksonConfig().viewsObjectMapper();
    @Captor
    private ArgumentCaptor<Map> requestParamsArgCaptor;

    @Before
    public void setup() {
        objectMapper = new JacksonConfig().viewsObjectMapper();
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
    }

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametrosComUsuariosIds_quandoPassarVariosUsuariosIds() {
        service.getLoginsLogoutsDeHoje(List.of(12, 1, 98), umPageRequest());

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
        service.getLoginsLogoutsDeHoje(null, umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).doesNotContainKey(USUARIOS_IDS_REQUEST_PARAM);
    }

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametroUsuarioIdsComStringVazia_quandoUsuarioIdsForVazio() {
        service.getLoginsLogoutsDeHoje(Set.of(), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).containsEntry(USUARIOS_IDS_REQUEST_PARAM, "");
    }

    @Test
    public void getLoginsLogoutsDeHoje_devePassarParametroUsuarioIdsUmIdVazia_quandoUsuarioIdsTiverApenasUmId() {
        service.getLoginsLogoutsDeHoje(Set.of(2002), umPageRequest());

        verify(client, times(1)).getLoginsLogoutsDeHoje(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).containsEntry(USUARIOS_IDS_REQUEST_PARAM, "2002");
    }

    @NotNull
    private PageRequest umPageRequest() {
        return new PageRequest(6, 35, "nomeFantasia", "DESC");
    }
}
