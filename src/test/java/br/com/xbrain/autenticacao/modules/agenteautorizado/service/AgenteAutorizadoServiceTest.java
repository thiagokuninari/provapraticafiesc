package br.com.xbrain.autenticacao.modules.agenteautorizado.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.agenteautorizado.client.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static br.com.xbrain.autenticacao.modules.agenteautorizado.helper.UsuarioDtoVendasHelper.umUsuarioDtoVendas;
import static br.com.xbrain.autenticacao.modules.agenteautorizado.helper.UsuarioDtoVendasHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.usuariosDoAa1300ComEquipesDeVendas;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.usuariosMesmoSegmentoAgenteAutorizado1300;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioServiceHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AgenteAutorizadoServiceTest {

    @InjectMocks
    private AgenteAutorizadoService service;
    @Mock
    private AgenteAutorizadoClient client;
    @Mock
    private AutenticacaoService autenticacaoService;

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
    public void buscarTodosUsuariosDosAas_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .buscarTodosUsuariosDosAas(List.of(1, 2), true);

        assertThatThrownBy(() -> service.buscarTodosUsuariosDosAas(List.of(1, 2), true))
            .isInstanceOf(IntegracaoException.class);

        verify(client).buscarTodosUsuariosDosAas(List.of(1, 2), true);
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
    public void findAgenteAutorizadoByUsuarioId_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .findAgenteAutorizadoByUsuarioId(1);

        assertThatThrownBy(() -> service.findAgenteAutorizadoByUsuarioId(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).findAgenteAutorizadoByUsuarioId(1);
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
    public void findAgentesAutorizadosByUsuariosIds_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true));

        assertThatThrownBy(() -> service.findAgentesAutorizadosByUsuariosIds(List.of(1), true))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#044 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(client).findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true));
    }

    @Test
    public void findAgentesAutorizadosByUsuariosIds_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true));

        assertThatThrownBy(() -> service.findAgentesAutorizadosByUsuariosIds(List.of(1), true))
            .isInstanceOf(IntegracaoException.class);

        verify(client).findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true));
    }

    @Test
    public void getUsuariosAaAtivoComVendedoresD2D_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoSolicitado() {
        doReturn(usuariosDoAa1300ComEquipesDeVendas())
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
            .hasMessage("#054 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");
    }

    @Test
    public void recuperarColaboradoresDoAgenteAutorizado_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .recuperarColaboradoresDoAgenteAutorizado("767.581.560-28");

        assertThatThrownBy(() -> service.recuperarColaboradoresDoAgenteAutorizado("767.581.560-28"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#054 - Desculpe, ocorreu um erro interno. Contate o administrador.");

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

    @Test
    public void getUsuariosAaAtivoSemVendedoresD2D_integracaoException_seApiIndisponivel() {
        when(client.getUsuariosAaAtivoSemVendedoresD2D(eq(1)))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/agente-autorizado/api/usuarios-sem-d2d/{1}", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getUsuariosAaAtivoSemVendedoresD2D(1))
            .withMessage("#012 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void getUsuariosAaAtivoSemVendedoresD2D_integracaoException_seIdNaoInformado() {
        when(client.getUsuariosAaAtivoSemVendedoresD2D(eq(null)))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo usuarioId é obrigatório.\",\"field\":usuarioId]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getUsuariosAaAtivoSemVendedoresD2D(null));
    }

    @Test
    public void getUsuariosAaAtivoSemVendedoresD2D_retornaListaDeUsuarios_quandoSolicitado() {
        when(client.getUsuariosAaAtivoSemVendedoresD2D(eq(1)))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse()));

        assertThat(service.getUsuariosAaAtivoSemVendedoresD2D(1))
            .extracting("id", "nome", "agenteAutorizadoId", "email", "equipeVendaId")
            .containsExactly(tuple(1, "TESTE", 1, "TESTE@XBRAIN.COM.BR", 1));
    }

    @Test
    public void getUsuariosAaAtivoSemVendedoresD2D_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosAaAtivoSemVendedoresD2D(1);

        assertThatThrownBy(() -> service.getUsuariosAaAtivoSemVendedoresD2D(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getUsuariosAaAtivoSemVendedoresD2D(1);
    }

    @Test
    public void ativarUsuario_deveAtivarAgenteAutorizado_quandoSolicitado() {
        service.ativarUsuario(1);

        verify(client).ativarUsuario(1);
    }

    @Test
    public void ativarUsuario_deveLancarException_quandoRetornarErroNaApi() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(client)
            .ativarUsuario(1);

        assertThatThrownBy(() -> service.ativarUsuario(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#056 - Ocorreu um erro ao tentar ativar o usuário. Contate o administrador.");

        verify(client).ativarUsuario(1);
    }

    @Test
    public void ativarUsuario_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .ativarUsuario(1);

        assertThatThrownBy(() -> service.ativarUsuario(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#056 - Ocorreu um erro ao tentar ativar o usuário. Contate o administrador.");

        verify(client).ativarUsuario(1);
    }

    @Test
    public void inativarUsuario_deveInativarAgenteAutorizado_quandoSolicitado() {
        service.inativarUsuario(2);

        verify(client).inativarUsuario(2);
    }

    @Test
    public void inativarUsuario_deveLancarException_quandoRetornarErroNaApi() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(client)
            .inativarUsuario(1);

        assertThatThrownBy(() -> service.inativarUsuario(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#057 - Ocorreu um erro ao tentar ativar o usuário. Contate o administrador.");

        verify(client).inativarUsuario(1);
    }

    @Test
    public void inativarUsuario_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .inativarUsuario(1);

        assertThatThrownBy(() -> service.inativarUsuario(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#057 - Ocorreu um erro ao tentar ativar o usuário. Contate o administrador.");

        verify(client).inativarUsuario(1);
    }

    @Test
    public void atualizarEmailSocioPrincipalInativo_deveAtualizarEmailSocioPrincipal_quandoSolicitado() {
        service.atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        );

        verify(client).atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        );
    }

    @Test
    public void atualizarEmailSocioPrincipalInativo_deveLancarException_quandoRetornarErroNaApi() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(client)
            .atualizarEmailSocioPrincipalInativo(
                "novoemailsocioteste@xbrain.com.br",
                "antigoemailsocioteste@xbrain.com.br",
                1
            );

        assertThatThrownBy(() -> service.atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        ))
            .isInstanceOf(IntegracaoException.class);

        verify(client).atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        );
    }

    @Test
    public void atualizarEmailSocioPrincipalInativo_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .atualizarEmailSocioPrincipalInativo(
                "novoemailsocioteste@xbrain.com.br",
                "antigoemailsocioteste@xbrain.com.br",
                1
            );

        assertThatThrownBy(() -> service.atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        ))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#051 - Não foi possível atualizar o e-mail do sócio no Parceiros Online.");

        verify(client).atualizarEmailSocioPrincipalInativo(
            "novoemailsocioteste@xbrain.com.br",
            "antigoemailsocioteste@xbrain.com.br",
            1
        );
    }

    @Test
    public void getIdsUsuariosSubordinados_deveRetornarIdsUsuariosSubordinados_quandoIncluirProprioForTrue() {
        when(autenticacaoService.getUsuarioId()).thenReturn(any());
        when(client.getIdUsuariosDoUsuario(Map.of()))
            .thenReturn(Set.of(1, 2));

        assertThat(service.getIdsUsuariosSubordinados(true)).isEqualTo(Set.of(1, 2));

        verify(autenticacaoService).getUsuarioId();
        verify(client).getIdUsuariosDoUsuario(Map.of());
    }

    @Test
    public void getIdsUsuariosSubordinados_deveRetornarListaVazia_quandoIncluirProprioForFalse() {
        assertThat(service.getIdsUsuariosSubordinados(false))
            .isEqualTo(Set.of());

        verify(autenticacaoService, never()).getUsuarioId();
        verify(client).getIdUsuariosDoUsuario(Map.of());
    }

    @Test
    public void getIdsUsuariosSubordinados_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getIdUsuariosDoUsuario(Map.of());

        assertThatThrownBy(() -> service.getIdsUsuariosSubordinados(true))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#027 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getIdUsuariosDoUsuario(Map.of());
    }

    @Test
    public void getIdsUsuariosSubordinados_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getIdUsuariosDoUsuario(Map.of());

        assertThatThrownBy(() -> service.getIdsUsuariosSubordinados(false))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getIdUsuariosDoUsuario(Map.of());
    }

    @Test
    public void getIdsUsuariosSubordinadosByFiltros_deveRetornarIdsUsuariosSubordinados_quandoFiltrosForemFornecidos() {
        when(client.getIdsUsuariosPermitidosDoUsuario(any()))
            .thenReturn(List.of(1, 2));

        assertThat(service.getIdsUsuariosSubordinadosByFiltros(umPublicoAlvoComunicadoFiltros()))
            .isEqualTo(List.of(1, 2));

        verify(client).getIdsUsuariosPermitidosDoUsuario(any());
    }

    @Test
    public void getIdsUsuariosSubordinadosByFiltros_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getIdsUsuariosPermitidosDoUsuario(any());

        assertThatThrownBy(() -> service.getIdsUsuariosSubordinadosByFiltros(umPublicoAlvoComunicadoFiltros()))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#027 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getIdsUsuariosPermitidosDoUsuario(any());
    }

    @Test
    public void getIdsUsuariosSubordinadosByFiltros_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getIdsUsuariosPermitidosDoUsuario(any());

        assertThatThrownBy(() -> service.getIdsUsuariosSubordinadosByFiltros(umPublicoAlvoComunicadoFiltros()))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getIdsUsuariosPermitidosDoUsuario(any());
    }

    @Test
    public void getAaByCpnj_deveRetornarAgenteAutorizadoResponse_quandoCnpjForFornecido() {
        var request = Map.of("cnpj", "78.620.184/0001-80");
        when(client.getAaByCpnj(request))
            .thenReturn(umAgenteAutorizadoResponse());

        assertThat(service.getAaByCpnj("78.620.184/0001-80"))
            .extracting("razaoSocial", "cnpj")
            .containsExactly("AA TESTE", "78.620.184/0001-80");

        verify(client).getAaByCpnj(request);
    }

    @Test
    public void getAaByCpnj_deveLancarIntegracaoException_quandoApiIndisponivel() {
        var request = Map.of("cnpj", "78.620.184/0001-80");

        doThrow(RetryableException.class)
            .when(client)
            .getAaByCpnj(request);

        assertThatThrownBy(() -> service.getAaByCpnj("78.620.184/0001-80"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#002 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getAaByCpnj(request);
    }

    @Test
    public void getAaByCpnj_deveLancarIntegracaoException_quandoErroNaApi() {
        var request = Map.of("cnpj", "78.620.184/0001-80");

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getAaByCpnj(request);

        assertThatThrownBy(() -> service.getAaByCpnj("78.620.184/0001-80"))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getAaByCpnj(request);
    }

    @Test
    public void getAaById_deveRetornarAgenteAutorizadoResponse_quandoIdForFornecido() {
        when(client.getAaById(1))
            .thenReturn(umAgenteAutorizadoResponse());

        assertThat(service.getAaById(1))
            .extracting("razaoSocial", "cnpj")
            .containsExactly("AA TESTE", "78.620.184/0001-80");

        verify(client).getAaById(1);
    }

    @Test
    public void getAaById_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getAaById(1);

        assertThatThrownBy(() -> service.getAaById(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#005 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getAaById(1);
    }

    @Test
    public void getAaById_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getAaById(1);

        assertThatThrownBy(() -> service.getAaById(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getAaById(1);
    }

    @Test
    public void getEmpresasPermitidas_deveRetornarListaEmpresa_quandoIdForFornecido() {
        when(client.getEmpresasPermitidas(1))
            .thenReturn(List.of(umaEmpresaResponse()));

        assertThat(service.getEmpresasPermitidas(1))
            .extracting("id", "nome", "codigo")
            .containsExactly(
                tuple(1, "LTDA", CodigoEmpresa.NET)
            );

        verify(client).getEmpresasPermitidas(1);
    }

    @Test
    public void getEmpresasPermitidas_deveRetornarListaVazia_quandoApiIndisponivel() {
        doThrow(RuntimeException.class)
            .when(client)
            .getEmpresasPermitidas(1);

        assertThat(service.getEmpresasPermitidas(1))
            .isEqualTo(List.of());

        verify(client).getEmpresasPermitidas(1);
    }

    @Test
    public void getEstruturaByUsuarioIdAndAtivo_deveRetornarEstrutura_quandoUsuarioIdForFornecidoEUsuarioAtivo() {
        when(client.getEstruturaByUsuarioIdAndAtivo(1))
            .thenReturn("AA");

        assertThat(service.getEstruturaByUsuarioIdAndAtivo(1))
            .isEqualTo("AA");

        verify(client).getEstruturaByUsuarioIdAndAtivo(1);
    }

    @Test
    public void getEstruturaByUsuarioIdAndAtivo_deveRetornarNull_quandoApiIndisponivel() {
        doThrow(RuntimeException.class)
            .when(client)
            .getEstruturaByUsuarioIdAndAtivo(1);

        assertThat(service.getEstruturaByUsuarioIdAndAtivo(1))
            .isEqualTo(null);

        verify(client).getEstruturaByUsuarioIdAndAtivo(1);
    }

    @Test
    public void getEstruturaByUsuarioId_deveRetornarEstrutura_quandoUsuarioIdForFornecido() {
        when(client.getEstruturaByUsuarioId(1))
            .thenReturn("AA");

        assertThat(service.getEstruturaByUsuarioId(1))
            .isEqualTo("AA");

        verify(client).getEstruturaByUsuarioId(1);
    }

    @Test
    public void getEstruturaByUsuarioId_deveRetornarNull_quandoApiIndisponivel() {
        doThrow(RuntimeException.class)
            .when(client)
            .getEstruturaByUsuarioId(1);

        assertThat(service.getEstruturaByUsuarioId(1))
            .isEqualTo(null);

        verify(client).getEstruturaByUsuarioId(1);
    }

    @Test
    public void getEstruturaByAgenteAutorizadoId_deveRetornarEstrutura_quandoUsuarioIdForFornecido() {
        when(client.getEstruturaByAgenteAutorizadoId(1))
            .thenReturn("AA");

        assertThat(service.getEstruturaByAgenteAutorizadoId(1))
            .isEqualTo(Optional.of("AA"));

        verify(client).getEstruturaByAgenteAutorizadoId(1);
    }

    @Test
    public void getEstruturaByAgenteAutorizadoId_deveRetornarVazio_quandoApiIndisponivel() {
        doThrow(RuntimeException.class)
            .when(client)
            .getEstruturaByAgenteAutorizadoId(1);

        assertThat(service.getEstruturaByAgenteAutorizadoId(1))
            .isEqualTo(Optional.empty());

        verify(client).getEstruturaByAgenteAutorizadoId(1);
    }

    @Test
    public void existeAaAtivoBySocioEmail_deveRetornarTrue_quandoUsuarioEmailForFornecido() {
        when(client.existeAaAtivoBySocioEmail("usuario@xbrain.com.br"))
            .thenReturn(true);

        assertThat(service.existeAaAtivoBySocioEmail("usuario@xbrain.com.br"))
            .isEqualTo(true);

        verify(client).existeAaAtivoBySocioEmail("usuario@xbrain.com.br");
    }

    @Test
    public void existeAaAtivoBySocioEmail_deveRetornarFalse_quandoUsuarioEmailForFornecido() {
        when(client.existeAaAtivoBySocioEmail("usuario@xbrain.com.br"))
            .thenReturn(false);

        assertThat(service.existeAaAtivoBySocioEmail("usuario@xbrain.com.br"))
            .isEqualTo(false);

        verify(client).existeAaAtivoBySocioEmail("usuario@xbrain.com.br");
    }

    @Test
    public void existeAaAtivoBySocioEmail_deveRetornarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .existeAaAtivoBySocioEmail("usuario@xbrain.com.br");

        assertThatThrownBy(() -> service.existeAaAtivoBySocioEmail("usuario@xbrain.com.br"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#019 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).existeAaAtivoBySocioEmail("usuario@xbrain.com.br");
    }

    @Test
    public void existeAaAtivoByUsuarioId_deveRetornarTrue_quandoUsuarioEmailForFornecido() {
        when(client.existeAaAtivoByUsuarioId(1))
            .thenReturn(true);

        assertThat(service.existeAaAtivoByUsuarioId(1))
            .isEqualTo(true);

        verify(client).existeAaAtivoByUsuarioId(1);
    }

    @Test
    public void existeAaAtivoByUsuarioId_deveRetornarFalse_quandoUsuarioEmailForFornecido() {
        when(client.existeAaAtivoByUsuarioId(1))
            .thenReturn(false);

        assertThat(service.existeAaAtivoByUsuarioId(1))
            .isEqualTo(false);

        verify(client).existeAaAtivoByUsuarioId(1);
    }

    @Test
    public void existeAaAtivoByUsuarioId_deveRetornarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .existeAaAtivoByUsuarioId(1);

        assertThatThrownBy(() -> service.existeAaAtivoByUsuarioId(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#019 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).existeAaAtivoByUsuarioId(1);
    }

    @Test
    public void getAasPermitidos_deveRetornarListaIdsAasPermitidos_quandoUsuarioIdForFornecido() {
        when(client.getAasPermitidos(1))
            .thenReturn(List.of(1, 2));

        assertThat(service.getAasPermitidos(1))
            .isEqualTo(List.of(1, 2));

        verify(client).getAasPermitidos(1);
    }

    @Test
    public void getAasPermitidos_deveRetornarVazio_quandoApiIndisponivel() {
        doThrow(RuntimeException.class)
            .when(client)
            .getAasPermitidos(1);

        assertThat(service.getAasPermitidos(1))
            .isEqualTo(List.of());

        verify(client).getAasPermitidos(1);
    }

    @Test
    public void getAgentesAutorizadosPermitidos_deveRetornarListaIdsAasPermitidos_quandoCodigoNivelForAgenteAutorizado() {
        when(client.getAasPermitidos(1))
            .thenReturn(List.of(1, 2));

        var usuario = umUsuarioSocioPrincipalEAa();

        assertThat(service.getAgentesAutorizadosPermitidos(usuario))
            .isEqualTo(List.of(1, 2));

        verify(client).getAasPermitidos(1);
    }

    @Test
    public void getAgentesAutorizadosPermitidos_deveRetornarListaVazia_quandoCodigoNivelNaoForAgenteAutorizado() {
        var usuario = umUsuarioSocioPrincipalEAa();
        usuario.getCargo().getNivel().setCodigo(CodigoNivel.MSO);

        assertThat(service.getAgentesAutorizadosPermitidos(usuario))
            .isEqualTo(List.of());

        verify(client, never()).getAasPermitidos(1);
    }

    @Test
    public void getIdUsuariosPorAa_deveRetornarListaIdUsuariosPorAa_quandoCnpjForFornecido() {
        var request = Map.of("cnpj", "78.620.184/0001-80");
        when(client.getAaByCpnj(request))
            .thenReturn(umAgenteAutorizadoResponse());

        when(client.getUsuariosByAaId(10, true))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse()));

        assertThat(service.getIdUsuariosPorAa("78.620.184/0001-80", true))
            .isEqualTo(List.of(1));

        verify(client).getAaByCpnj(request);
        verify(client).getUsuariosByAaId(10, true);
    }

    @Test
    public void getIdUsuariosPorAa_deveLancarIntegracaoException_quandoApiIndisponivel() {
        var request = Map.of("cnpj", "78.620.184/0001-80");

        doThrow(RetryableException.class)
            .when(client)
            .getAaByCpnj(request);

        assertThatThrownBy(() -> service.getIdUsuariosPorAa("78.620.184/0001-80", true))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#002 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getAaByCpnj(request);
    }

    @Test
    public void getIdUsuariosPorAa_deveLancarIntegracaoException_quandoErroNaApi() {
        var request = Map.of("cnpj", "78.620.184/0001-80");

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getAaByCpnj(request);

        assertThatThrownBy(() -> service.getIdUsuariosPorAa("78.620.184/0001-80", true))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getAaByCpnj(request);
    }

    @Test
    public void getUsuariosByAaId_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoAaIdForFornecido() {
        when(client.getUsuariosByAaId(1, true))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse()));

        assertThat(service.getUsuariosByAaId(1, true))
            .extracting("id", "nome", "email", "equipeVendaId", "agenteAutorizadoId")
            .containsExactly(tuple(1, "TESTE", "TESTE@XBRAIN.COM.BR", 1, 1));

        verify(client).getUsuariosByAaId(1, true);
    }

    @Test
    public void getUsuariosByAaId_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosByAaId(1, true);

        assertThatThrownBy(() -> service.getUsuariosByAaId(1, true))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#003 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosByAaId(1, true);
    }

    @Test
    public void getUsuariosByAaId_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosByAaId(1, true);

        assertThatThrownBy(() -> service.getUsuariosByAaId(1, true))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getUsuariosByAaId(1, true);
    }

    @Test
    public void getUsuariosByAasIds_deveRetornarListaUsuarioAgenteAutorizadoResponse_quandoAaIdForFornecido() {
        var aaIds = List.of(1);
        when(client.getUsuariosByAasIds(aaIds))
            .thenReturn(Map.of(1, 2));

        assertThat(service.getUsuariosByAasIds(aaIds))
            .extracting("id", "aaId")
            .containsExactly(tuple(1, 2));

        verify(client).getUsuariosByAasIds(aaIds);
    }

    @Test
    public void getUsuariosByAasIds_deveLancarIntegracaoException_quandoApiIndisponivel() {
        var aaIds = List.of(1);
        doThrow(RetryableException.class)
            .when(client)
            .getUsuariosByAasIds(aaIds);

        assertThatThrownBy(() -> service.getUsuariosByAasIds(aaIds))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#003 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getUsuariosByAasIds(aaIds);
    }

    @Test
    public void getUsuariosByAasIds_deveLancarIntegracaoException_quandoErroNaApi() {
        var aaIds = List.of(1);
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getUsuariosByAasIds(aaIds);

        assertThatThrownBy(() -> service.getUsuariosByAasIds(aaIds))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getUsuariosByAasIds(aaIds);
    }

    @Test
    public void getUsuariosIdsByAaId_deveRetornarListaUsuarioIds_quandoAaIdForFornecido() {
        when(client.getUsuariosByAaId(1, true))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse()));

        assertThat(service.getUsuariosIdsByAaId(1, true))
            .isEqualTo(List.of(1));

        verify(client).getUsuariosByAaId(1, true);
    }

    @Test
    public void getAgenteAutorizadosUsuarioDtosByUsuarioIds_deveRetornarListaAgenteAutorizadoUsuarioDto_quandoRequestFornecido() {
        var request = umUsuarioRequest();
        var response = umAgenteAutorizadoUsuarioDto();

        when(client.getAgenteAutorizadosUsuarioDtosByUsuarioIds(request))
            .thenReturn(List.of(response));

        assertThat(service.getAgenteAutorizadosUsuarioDtosByUsuarioIds(request))
            .extracting("usuarioId", "cnpj", "razaoSocial")
            .containsExactly(tuple(2, "78300110000166", "Razao Social"));

        verify(client).getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);
    }

    @Test
    public void getAgenteAutorizadosUsuarioDtosByUsuarioIds_deveLancarIntegracaoException_quandoApiIndisponivel() {
        var request = umUsuarioRequest();

        doThrow(RetryableException.class)
            .when(client)
            .getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);

        assertThatThrownBy(() -> service.getAgenteAutorizadosUsuarioDtosByUsuarioIds(request))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#033 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);
    }

    @Test
    public void getAgenteAutorizadosUsuarioDtosByUsuarioIds_deveLancarIntegracaoException_quandoErroNaApi() {
        var request = umUsuarioRequest();

        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);

        assertThatThrownBy(() -> service.getAgenteAutorizadosUsuarioDtosByUsuarioIds(request))
            .isInstanceOf(IntegracaoException.class);

        verify(client).getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);
    }

    @Test
    public void findAgentesAutorizadosPapIndireto_deveRetornarUsuariosDtoVendas_quandoSolicitado() {
        doReturn(List.of(umOutroUsuarioDtoVendas()))
            .when(client)
            .findUsuariosAgentesAutorizadosPapIndireto();

        assertThat(service.findUsuariosAgentesAutorizadosPapIndireto())
            .extracting(UsuarioDtoVendas::getId, UsuarioDtoVendas::getEmail, UsuarioDtoVendas::getAgenteAutorizadoCnpj,
                UsuarioDtoVendas::getAgenteAutorizadoRazaoSocial, UsuarioDtoVendas::getAgenteAutorizadoId)
            .containsExactly(tuple(1, "mso_analistaadm_claromovel_pessoal@net.com.br", "64.262.572/0001-21",
                "Razao Social Teste", 1));

        verify(client).findUsuariosAgentesAutorizadosPapIndireto();
    }

    @Test
    public void findAgentesAutorizadosPapIndireto_deveLancarException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(client)
            .findUsuariosAgentesAutorizadosPapIndireto();

        assertThatThrownBy(() -> service.findUsuariosAgentesAutorizadosPapIndireto())
            .isInstanceOf(IntegracaoException.class);

        verify(client).findUsuariosAgentesAutorizadosPapIndireto();
    }

    @Test
    public void findAgentesAutorizadosPapIndireto_deveLancarException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .findUsuariosAgentesAutorizadosPapIndireto();

        assertThatThrownBy(() -> service.findUsuariosAgentesAutorizadosPapIndireto())
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#058 - Ocorreu um erro ao tentar buscar usuários. Contate o administrador.");

        verify(client).findUsuariosAgentesAutorizadosPapIndireto();
    }

    private AgenteAutorizadoResponse umAgenteAutorizadoResponse() {
        return AgenteAutorizadoResponse.builder()
            .id("10")
            .razaoSocial("AA TESTE")
            .cnpj("78.620.184/0001-80")
            .build();
    }

    private UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse() {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(1)
            .nome("TESTE")
            .agenteAutorizadoId(1)
            .email("TESTE@XBRAIN.COM.BR")
            .equipeVendaId(1)
            .build();
    }

    private EmpresaResponse umaEmpresaResponse() {
        var empresaResponse = new EmpresaResponse();
        empresaResponse.setId(1);
        empresaResponse.setNome("LTDA");
        empresaResponse.setCodigo(CodigoEmpresa.NET);
        return empresaResponse;
    }

    private UsuarioRequest umUsuarioRequest() {
        return UsuarioRequest.builder()
            .usuarioIds(List.of(1, 2, 3))
            .build();
    }
}
