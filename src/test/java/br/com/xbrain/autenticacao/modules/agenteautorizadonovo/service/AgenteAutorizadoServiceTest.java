package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.agenteautorizado.client.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.agenteautorizadonovo.helper.UsuarioDtoVendasHelper.umUsuarioDtoVendas;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.todosUsuariosDoAgenteAutorizado1300ComEquipesDeVendas;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.usuariosMesmoSegmentoAgenteAutorizado1300;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioServiceHelper.umaListaDeAgenteAutorizadoResponse;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    public void getUsuariosAaAtivoComVendedoresD2D_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoSolicitado() {
        doReturn(todosUsuariosDoAgenteAutorizado1300ComEquipesDeVendas())
            .when(client)
            .getUsuariosAaAtivoComVendedoresD2D(1);

        assertThat(service.getUsuariosAaAtivoComVendedoresD2D(1))
            .extracting(UsuarioAgenteAutorizadoResponse::getId, UsuarioAgenteAutorizadoResponse::getNome,
                UsuarioAgenteAutorizadoResponse::getEmail, UsuarioAgenteAutorizadoResponse::getEquipeVendaId,
                UsuarioAgenteAutorizadoResponse::getAgenteAutorizadoId)
            .containsExactlyInAnyOrder(
                tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS", null, 999, null),
                tuple(131, "ANTONIO ARYLDO DE SOUZA RODRIGUES", null, 980, null),
                tuple(132, "LUIZ BARRETTO DE AZEVEDO NETO", null, 755, null),
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", null, 999, null),
                tuple(134, "PAULO JUNIO COLARES MIRANDA", null, null, null),
                tuple(135, "LEONARDO DOS SANTOS GONCALVES REIS", null, 999, null)
            );

        verify(client).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void getUsuariosAaAtivoComVendedoresD2D_deveLancarException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosAaAtivoComVendedoresD2D(1);

        assertThatThrownBy(() -> service.getUsuariosAaAtivoComVendedoresD2D(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#012 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void getUsuariosAaAtivoComVendedoresD2D_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosAaAtivoComVendedoresD2D(1);

        assertThatThrownBy(() -> service.getUsuariosAaAtivoComVendedoresD2D(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#012 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void recuperarColaboradoresDoAgenteAutorizado_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoSolicitado() {
        doReturn(List.of("usuarioteste@xbrain.com.br", "usuarioteste2@xbrain.com.br"))
            .when(client)
            .recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");

        assertThat(service.recuperarColaboradoresDoAgenteAutorizado("767.581.560-28"))
            .contains("usuarioteste@xbrain.com.br", "usuarioteste2@xbrain.com.br");

        verify(client).recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");
    }

    @Test
    public void recuperarColaboradoresDoAgenteAutorizado_deveLancarException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");

        assertThatThrownBy(() -> service.recuperarColaboradoresDoAgenteAutorizado("767.581.560-28"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#047 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");
    }

    @Test
    public void recuperarColaboradoresDoAgenteAutorizado_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");

        assertThatThrownBy(() -> service.recuperarColaboradoresDoAgenteAutorizado("767.581.560-28"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#047 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");
    }

    @Test
    public void getUsuariosByAaIdCanalDoUsuario_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoSolicitado() {
        doReturn(usuariosMesmoSegmentoAgenteAutorizado1300())
            .when(client)
            .getUsuariosByAaIdCanalDoUsuario(1, 2);

        assertThat(service.getUsuariosByAaIdCanalDoUsuario(1, 2))
            .extracting(UsuarioAgenteAutorizadoAgendamentoResponse::getId, UsuarioAgenteAutorizadoAgendamentoResponse::getNome,
                UsuarioAgenteAutorizadoAgendamentoResponse::getEquipeVendasNome,
                UsuarioAgenteAutorizadoAgendamentoResponse::getEquipeVendasId,
                UsuarioAgenteAutorizadoAgendamentoResponse::getSupervisorNome)
            .containsExactlyInAnyOrder(
                tuple(130, "JOÃO MARINHO DA SILVA DOS SANTOS", null, null, null),
                tuple(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", null, null, null),
                tuple(134, "MARIA DA SILVA SAURO SANTOS", null, null, null),
                tuple(135, "MARCOS AUGUSTO DA SILVA SANTOS", null, null, null)
            );

        verify(client).getUsuariosByAaIdCanalDoUsuario(1, 2);
    }

    @Test
    public void getUsuariosByAaIdCanalDoUsuario_deveLancarException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosByAaIdCanalDoUsuario(1, 2);

        assertThatThrownBy(() -> service.getUsuariosByAaIdCanalDoUsuario(1, 2))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#003 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosByAaIdCanalDoUsuario(1, 2);
    }

    @Test
    public void getUsuariosByAaIdCanalDoUsuario_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosByAaIdCanalDoUsuario(1, 2);

        assertThatThrownBy(() -> service.getUsuariosByAaIdCanalDoUsuario(1, 2))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#003 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosByAaIdCanalDoUsuario(1, 2);
    }

    @Test
    public void getUsuariosIdsSuperioresPol_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoSolicitado() {
        doReturn(List.of(2, 3, 4536, 765, 65))
            .when(client)
            .getUsuariosIdsSuperioresPol();

        assertThat(service.getUsuariosIdsSuperioresPol()).contains(2, 3, 4536, 765, 65);

        verify(client).getUsuariosIdsSuperioresPol();
    }

    @Test
    public void getUsuariosIdsSuperioresPol_deveLancarException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosIdsSuperioresPol();

        assertThatThrownBy(() -> service.getUsuariosIdsSuperioresPol())
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#012 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosIdsSuperioresPol();
    }

    @Test
    public void getUsuariosIdsSuperioresPol_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosIdsSuperioresPol();

        assertThatThrownBy(() -> service.getUsuariosIdsSuperioresPol())
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#012 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosIdsSuperioresPol();
    }

    private AgenteAutorizadoResponse umAgenteAutorizadoResponse() {
        return AgenteAutorizadoResponse.builder()
            .id("10")
            .razaoSocial("AA TESTE")
            .cnpj("78.620.184/0001-80")
            .build();
    }
}
