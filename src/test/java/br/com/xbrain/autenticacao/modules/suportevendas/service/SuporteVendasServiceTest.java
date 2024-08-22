package br.com.xbrain.autenticacao.modules.suportevendas.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.suportevendas.client.SuporteVendasClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SuporteVendasServiceTest {

    @InjectMocks
    private SuporteVendasService service;
    @Mock
    private SuporteVendasClient client;

    @Test
    public void desvincularGruposByUsuarioId_deveChamarClient_quandoNaoOcorrerErro() {
        assertThatCode(() -> service.desvincularGruposByUsuarioId(10))
            .doesNotThrowAnyException();

        verify(client).desvincularGruposByUsuarioId(10);
    }

    @Test
    public void desvincularGruposByUsuarioId_deveLancarException_quandoClientRetornarErro() {
        doThrow(new RetryableException("", null))
            .when(client).desvincularGruposByUsuarioId(anyInt());

        assertThatCode(() -> service.desvincularGruposByUsuarioId(10))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular grupo do usuário no suporte-vendas.");
    }

    @Test
    public void desvincularGruposByUsuarioId_deveLancarIntegracaoException_quandoClientRetornarErro() {
        doThrow(new HystrixBadRequestException("", null))
            .when(client).desvincularGruposByUsuarioId(anyInt());

        assertThatCode(() -> service.desvincularGruposByUsuarioId(10))
            .isInstanceOf(IntegracaoException.class);

        verify(client).desvincularGruposByUsuarioId(anyInt());
    }
}
