package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.agenteautorizado.client.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.agenteautorizadonovo.helper.UsuarioDtoVendasHelper.umUsuarioDtoVendas;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioServiceHelper.umaListaDeAgenteAutorizadoResponse;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgenteAutorizadoServiceTest {

    @InjectMocks
    private AgenteAutorizadoService service;
    @Mock
    private AgenteAutorizadoClient client;

    @Test
    public void buscarTodosUsuariosDosAas_integracaoException_seApiIndisponivel() {
        when(client.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(false)))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/todos-usuarios-dos-aas", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarTodosUsuariosDosAas(List.of(1), false))
            .withMessage("#031 - Desculpe, ocorreu um erro interno. Contate a administrador.");
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

    @Test
    public void findAgenteAutorizadoByUsuarioId_integracaoException_seApiIndisponivel() {
        when(client.findAgenteAutorizadoByUsuarioId(eq(1)))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/carteira/{1}/agentes-autorizados", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.findAgenteAutorizadoByUsuarioId(1))
            .withMessage("#044 - Desculpe, ocorreu um erro interno. Contate a administrador.");
    }

    @Test
    public void findAgenteAutorizadoByUsuarioId_integracaoException_seIdUsuarioNaoInformado() {
        when(client.findAgenteAutorizadoByUsuarioId(eq(null)))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo usuarioId é obrigatório.\",\"field\":usuarioId]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.findAgenteAutorizadoByUsuarioId(null));
    }

    @Test
    public void findAgenteAutorizadoByUsuarioId_retornaListaAgentesAutorizadosDoUsuario() {
        when(client.findAgenteAutorizadoByUsuarioId(eq(1)))
            .thenReturn(List.of(umAgenteAutorizadoResponse()));

        assertThat(service.findAgenteAutorizadoByUsuarioId(1))
            .extracting("id", "razaoSocial", "cnpj")
            .containsExactly(tuple("10", "AA TESTE", "78.620.184/0001-80"));
    }

    @Test
    public void findAgentesAutorizadosByUsuariosIds_deveRetornarTodosAasDosUsuarios_quandoTudoOk() {
        when(client.findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true)))
            .thenReturn(umaListaDeAgenteAutorizadoResponse());

        assertThat(service.findAgentesAutorizadosByUsuariosIds(List.of(1), true))
            .extracting("id", "cnpj", "razaoSocial", "situacao")
            .containsExactlyInAnyOrder(
                tuple("1", "00.000.0000/0001-00", "TESTE AA", "CONTRATO ATIVO"),
                tuple("3", "00.000.0000/0001-30", "TESTE AA INATIVO", "INATIVO"),
                tuple("4", "00.000.0000/0001-40", "TESTE AA REJEITADO", "REJEITADO"),
                tuple("2", "00.000.0000/0001-20", "OUTRO TESTE AA", "CONTRATO ATIVO"));
    }

    private AgenteAutorizadoResponse umAgenteAutorizadoResponse() {
        return AgenteAutorizadoResponse.builder()
            .id("10")
            .razaoSocial("AA TESTE")
            .cnpj("78.620.184/0001-80")
            .build();
    }
}
