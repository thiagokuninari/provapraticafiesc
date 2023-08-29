package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.client.UsuarioClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioClientServiceTest {

    @Mock
    private UsuarioClient client;
    @InjectMocks
    private UsuarioClientService service;

    @Test
    public void alterarSituacao_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(new RetryableException("Connection refused (Connection refused) executing "
            + "PUT http://localhost:8099/api/agentes-autorizados-usuario/1/alterar-situacao", new Date()))
            .when(client).alterarSituacao(eq(1));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.alterarSituacao(1))
            .withMessage("#039 - Desculpe, ocorreu um erro ao alterar a situação do usuário.");
    }

    @Test
    public void alterarSituacao_deveLancarIntegracaoException_quandoBadRequest() {
        doThrow(new HystrixBadRequestException("Id informado é nulo"))
            .when(client).alterarSituacao(eq(null));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.alterarSituacao(null));
    }
}
