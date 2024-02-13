package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorVendasClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRemanejamentoRequest;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_VAREJO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColaboradorVendasServiceTest {

    @Mock
    private ColaboradorVendasClient client;
    @InjectMocks
    private ColaboradorVendasService service;

    @Test
    public void limparCpfColaboradorVendas_deveChamarClient_quandoSolicitado() {
        service.limparCpfColaboradorVendas("usuarioteste@gmail.com");

        verify(client).limparCpfColaboradorVendas("usuarioteste@gmail.com");
    }

    @Test
    public void limparCpfColaboradorVendas_deveLancarException_quandoApiIndisponivel() {
        doThrow(HystrixBadRequestException.class)
            .when(client)
            .limparCpfColaboradorVendas("usuarioteste@gmail.com");

        assertThatThrownBy(() -> service.limparCpfColaboradorVendas("usuarioteste@gmail.com"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#053 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(client).limparCpfColaboradorVendas("usuarioteste@gmail.com");
    }

    @Test
    public void limparCpfColaboradorVendas_deveLancarException_quandoErroNaApi() {
        doThrow(RetryableException.class)
            .when(client)
            .limparCpfColaboradorVendas("usuarioteste@gmail.com");

        assertThatThrownBy(() -> service.limparCpfColaboradorVendas("usuarioteste@gmail.com"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#053 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(client).limparCpfColaboradorVendas("usuarioteste@gmail.com");
    }

    @Test
    public void getUsuariosAaFeederPorCargo_deveChamarClient_quandoSolicitado() {
        doReturn(List.of(1, 2, 3))
            .when(client)
            .getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));

        assertThat(service.getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO)))
            .contains(1, 2, 3);

        verify(client).getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));
    }

    @Test
    public void getUsuariosAaFeederPorCargo_deveLancarException_quandoApiIndisponivel() {
        doThrow(HystrixBadRequestException.class)
            .when(client)
            .getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));

        assertThatThrownBy(() -> service.getUsuariosAaFeederPorCargo(List.of(1, 2, 3),
            List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO)))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#045 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(client).getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));
    }

    @Test
    public void getUsuariosAaFeederPorCargo_deveLancarException_quandoErroNaApi() {
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));

        assertThatThrownBy(() -> service.getUsuariosAaFeederPorCargo(List.of(1, 2, 3),
            List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO)))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#045 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(client).getUsuariosAaFeederPorCargo(List.of(1, 2, 3), List.of(AGENTE_AUTORIZADO_VENDEDOR_VAREJO));
    }

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
