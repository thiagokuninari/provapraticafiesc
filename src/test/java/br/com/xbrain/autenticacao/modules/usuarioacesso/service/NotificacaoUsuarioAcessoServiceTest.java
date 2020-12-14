package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class NotificacaoUsuarioAcessoServiceTest {

    @InjectMocks
    private NotificacaoUsuarioAcessoService service;
    @Mock
    private NotificacaoUsuarioAcessoClient client;

    @Test
    public void countUsuariosLogadosPorPeriodo_deveChamarClient_quandoChamado() {
        when(client.countUsuariosLogadosPorPeriodo(umUsuarioLogadoRequest())).thenReturn(umaListaUsuariosLogados());

        assertThat(service.countUsuariosLogadosPorPeriodo(umUsuarioLogadoRequest())).isEqualTo(umaListaUsuariosLogados());

        verify(client, times(1)).countUsuariosLogadosPorPeriodo(any(UsuarioLogadoRequest.class));
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
}
