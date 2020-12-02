package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class NotificacaoUsuarioAcessoServiceTest {

    private static final LocalDateTime DATA_INICIAL = LocalDateTime.of(2020, 12, 1, 10, 0, 0);
    private static final LocalDateTime DATA_FINAL = LocalDateTime.of(2020, 12, 1, 16, 40, 0);

    @InjectMocks
    private NotificacaoUsuarioAcessoService service;
    @Mock
    private NotificacaoUsuarioAcessoClient client;
    @Captor
    private ArgumentCaptor<Map<String, Object>> requestParamsArgCaptor;

    @Test
    public void countUsuariosLogadosPorHora_deveRetornarListaVaziaENaoChamarOClient_quandoUsuariosIdsForListaVazia() {
        assertThat(service.countUsuariosLogadosPorHora(List.of(), DATA_INICIAL, DATA_FINAL))
            .isEmpty();

        verify(client, never()).countUsuariosLogadosPorHora(any());
    }

    @Test
    public void countUsuariosLogadosPorHora_deveRetornarListaVaziaENaoChamarOClient_quandoUsuariosIdsForNull() {
        assertThat(service.countUsuariosLogadosPorHora(null, DATA_INICIAL, DATA_FINAL))
            .isEmpty();

        verify(client, never()).countUsuariosLogadosPorHora(any());
    }

    @Test
    public void countUsuariosLogadosPorHora_devePassarParametros_quandoTiverUsuariosIds() {
        assertThat(service.countUsuariosLogadosPorHora(List.of(101, 102, 103), DATA_INICIAL, DATA_FINAL));

        verify(client, times(1)).countUsuariosLogadosPorHora(requestParamsArgCaptor.capture());

        assertThat(requestParamsArgCaptor.getValue()).contains(
            entry("dataInicio", "2020-12-01T10:00:00.000Z"),
            entry("dataFim", "2020-12-01T16:40:00.000Z"),
            entry("usuariosIds", "101,102,103"));
    }
}
