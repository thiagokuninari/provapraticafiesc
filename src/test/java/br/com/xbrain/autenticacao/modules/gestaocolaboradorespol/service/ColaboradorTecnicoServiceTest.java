package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorTecnicoClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRemanejamentoRequest;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ColaboradorTecnicoServiceTest {

    @InjectMocks
    private ColaboradorTecnicoService service;
    @Mock
    private ColaboradorTecnicoClient client;

    @Test
    public void atualizarUsuarioRemanejado_deveEnviarParaAtualizcaoDoRemanejamento_quandoSolicitado() {
        service.atualizarUsuarioRemanejado(new UsuarioRemanejamentoRequest());

        verify(client).atualizarUsuarioRemanejado(new UsuarioRemanejamentoRequest());
    }

    @Test
    public void atualizarUsuarioRemanejado_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(new RetryableException("Connection refused", new Date()))
            .when(client).atualizarUsuarioRemanejado(any());

        assertThatThrownBy(() -> service.atualizarUsuarioRemanejado(new UsuarioRemanejamentoRequest()))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#055 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void atualizarUsuarioRemanejado_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client).atualizarUsuarioRemanejado(any());

        assertThatThrownBy(() -> service.atualizarUsuarioRemanejado(new UsuarioRemanejamentoRequest()))
            .isInstanceOf(IntegracaoException.class);
    }
}
