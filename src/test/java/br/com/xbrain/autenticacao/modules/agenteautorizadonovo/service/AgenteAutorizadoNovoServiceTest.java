package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.agenteautorizadonovo.helper.UsuarioDtoVendasHelper.umUsuarioDtoVendas;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgenteAutorizadoNovoServiceTest {

    @InjectMocks
    private AgenteAutorizadoNovoService service;
    @Mock
    private AgenteAutorizadoNovoClient client;

    @Test
    public void buscarTodosUsuariosDosAas_integracaoException_seApiIndisponivel() {
        when(client.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(false)))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/todos-usuarios-dos-aas", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarTodosUsuariosDosAas(List.of(1), false))
            .withMessage("#033 - Desculpe, ocorreu um erro interno. Contate a administrador.");
    }

    @Test
    public void buscarTodosUsuariosDosAas_integracaoException_seFiltrosObrigatoriosNaoInformados() {
        when(client.buscarTodosUsuariosDosAas(eq(null), eq(false)))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo aasIds é obrigatório.\",\"field\":aasIds}]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarTodosUsuariosDosAas(null, false));
    }

    @Test
    public void buscarTodosUsuariosDosAas_usuarioDtoVendas_seSolicitado() {
        when(client.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(false)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));

        assertThat(service.buscarTodosUsuariosDosAas(List.of(1), false))
            .extracting("id")
            .containsExactly(1);
    }
}
