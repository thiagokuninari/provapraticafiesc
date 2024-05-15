package br.com.xbrain.autenticacao.modules.suportevendas.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.suportevendas.client.SuporteVendasClient;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuporteVendasServiceTest {

    @InjectMocks
    private SuporteVendasService service;
    @Mock
    private SuporteVendasClient client;

    @Test
    public void existsGrupoByUsuarioAndOrganizacaoNot_deveRetornarTrue_quandoClientRetornarTrue() {
        when(client.existsGrupoByUsuarioAndOrganizacaoNot(10, 20))
            .thenReturn(true);

        assertTrue(service.existsGrupoByUsuarioAndOrganizacaoNot(10, 20));
    }

    @Test
    public void existsGrupoByUsuarioAndOrganizacaoNot_deveLancarException_quandoClientRetornarErro() {
        doThrow(new RetryableException("", null))
            .when(client).existsGrupoByUsuarioAndOrganizacaoNot(anyInt(), anyInt());

        assertThatCode(() -> service.existsGrupoByUsuarioAndOrganizacaoNot(10, 20))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao verificar grupo do usuário no suporte-vendas.");
    }
}
