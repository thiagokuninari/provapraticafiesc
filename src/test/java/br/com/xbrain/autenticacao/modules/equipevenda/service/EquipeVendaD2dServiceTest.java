package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.helper.EquipeVendasHelper.umaEquipeVendaDto;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ASSISTENTE_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.umUsuarioResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EquipeVendaD2dServiceTest {

    @InjectMocks
    private EquipeVendaD2dService equipeVendaD2dService;
    @Mock
    private EquipeVendaD2dClient equipeVendaD2dClient;

    @Test
    public void verificaPausaEmAndamento_deveRetornarTrue_quandoPausaEmAndamento() {
        when(equipeVendaD2dClient.verificarPausaEmAndamento("pausa"))
            .thenReturn(true);

        assertThat(equipeVendaD2dService.verificaPausaEmAndamento("pausa"))
            .isTrue();

        verify(equipeVendaD2dClient).verificarPausaEmAndamento("pausa");
    }

    @Test
    public void verificaPausaEmAndamento_deveRetornarFalse_quandoPausaNaoEstiverEmAndamento() {
        when(equipeVendaD2dClient.verificarPausaEmAndamento("pausa"))
            .thenReturn(false);

        assertThat(equipeVendaD2dService.verificaPausaEmAndamento("pausa"))
            .isFalse();

        verify(equipeVendaD2dClient).verificarPausaEmAndamento("pausa");
    }

    @Test
    public void verificaPausaEmAndamento_deveLancarIntegracaoException_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.verificarPausaEmAndamento(anyString()))
            .thenThrow(new RuntimeException());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.verificaPausaEmAndamento(anyString()))
            .withMessage("#004 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient).verificarPausaEmAndamento(anyString());
    }

    @Test
    public void getEquipeVendas_deveRetornarListaEquipeVendaDto_quandoUsuarioTiverEquipe() {
        when(equipeVendaD2dClient.getUsuario(anyMap()))
            .thenReturn(List.of(umaEquipeVendaDto(), umaEquipeVendaDto()));

        assertThat(equipeVendaD2dService.getEquipeVendas(1))
            .extracting("id", "descricao", "canalVenda")
            .containsExactly(tuple(1, "Equipe Teste 2", "TELEVENDAS"),
                tuple(1, "Equipe Teste 2", "TELEVENDAS"));

        verify(equipeVendaD2dClient).getUsuario(anyMap());
    }

    @Test
    public void getEquipeVendas_deveLancarIntegracaoException_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.getUsuario(anyMap()))
            .thenThrow(new RuntimeException());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendas(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient).getUsuario(anyMap());
    }

    @Test
    public void getEquipeVendasComSupervisor_deveRetornarListaEquipeVendaDto_quandoUsuarioComSupervisorTiverEquipe() {
        when(equipeVendaD2dClient.getUsuarioComSupervisor(anyMap()))
            .thenReturn(List.of(umaEquipeVendaSupervisorDto(1),
                umaEquipeVendaSupervisorDto(2)));

        assertThat(equipeVendaD2dService.getEquipeVendasComSupervisor(1))
            .extracting("id", "descricao", "canalVenda", "supervisorNome")
            .containsExactly(tuple(1, "descricao", "AGENTE AUTORIZADO", "nome supervisor"),
                tuple(2, "descricao", "AGENTE AUTORIZADO", "nome supervisor"));

        verify(equipeVendaD2dClient).getUsuarioComSupervisor(anyMap());
    }

    @Test
    public void getEquipeVendasComSupervisor_deveLancarIntegracaoException_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.getUsuarioComSupervisor(anyMap()))
            .thenThrow(RetryableException.class);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasComSupervisor(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient).getUsuarioComSupervisor(anyMap());
    }

    @Test
    public void getUsuariosPermitidos_deveRetornarListaEquipeVendaUsuarioResponse_quandoUsuarioPermitido() {
        when(equipeVendaD2dClient.getUsuariosPermitidos(List.of(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)))
            .thenReturn(List.of(umaEquipeVendaUsuarioResponse(1), umaEquipeVendaUsuarioResponse(2)));

        assertThat(equipeVendaD2dService.getUsuariosPermitidos(List.of(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)))
            .extracting("id", "usuarioNome", "cargoNome", "equipeVendaId", "usuarioId")
            .containsExactly(tuple(1, "nome usuario", "AGENTE AUTORIZADO GERENTE", 1, 1),
                tuple(2, "nome usuario", "AGENTE AUTORIZADO GERENTE", 1, 1));

        verify(equipeVendaD2dClient).getUsuariosPermitidos(List.of(CodigoCargo.AGENTE_AUTORIZADO_GERENTE));
    }

    @Test
    public void getUsuariosPermitidos_deveLancarIntegracaoException_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.getUsuariosPermitidos(anyList()))
            .thenThrow(new RuntimeException());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getUsuariosPermitidos(anyList()))
            .withMessage("#017 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient).getUsuariosPermitidos(anyList());
    }

    @Test
    public void getVendedoresPorEquipe_deveRetornarListaIds_quandoVendedorTiverEquipe() {
        when(equipeVendaD2dClient.getVendedoresPorEquipe(anyMap()))
            .thenReturn(List.of(umSelectResponse(1), umSelectResponse(2)));
        var captor = ArgumentCaptor.forClass(Map.class);

        assertThat(equipeVendaD2dService.getVendedoresPorEquipe(List.of(1, 2)))
            .isEqualTo(List.of(1, 2));

        verify(equipeVendaD2dClient).getVendedoresPorEquipe(captor.capture());

        var capturedMap = captor.getValue();
        assertThat(capturedMap.get("equipeVendaIds")).isEqualTo(List.of(1, 2));
        assertThat(capturedMap.get("ativo")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void getVendedoresPorEquipe_deveRetornarListaVazia_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.getVendedoresPorEquipe(anyMap()))
            .thenThrow(new RuntimeException());

        assertThat(equipeVendaD2dService.getVendedoresPorEquipe(anyList()))
            .isEqualTo(List.of());

        verify(equipeVendaD2dClient).getVendedoresPorEquipe(anyMap());
    }

    @Test
    public void filtrarUsuariosQuePodemAderirAEquipe_deveRetornarListaUsuarioResponse_quandoUsuarioTiverEquipe() {
        when(equipeVendaD2dClient.filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(List.of(1, 2), 1))
            .thenReturn(List.of(1, 2));

        assertThat(equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(List.of(
            umUsuarioResponse(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO)), 1))
            .extracting("id", "nome", "situacao", "codigoCargo")
            .containsExactly(tuple(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
                tuple(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO));

        verify(equipeVendaD2dClient).filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(List.of(1, 2), 1);
    }

    @Test
    public void filtrarUsuariosQuePodemAderirAEquipe_deveRetornarListaVazia_quandoHouverErroNaApi() {
        when(equipeVendaD2dClient.filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(List.of(1, 2), 1))
            .thenThrow(new RuntimeException());

        assertThat(equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(List.of(
            umUsuarioResponse(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
            umUsuarioResponse(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO)
        ), 1))
            .isEqualTo(List.of());

        verify(equipeVendaD2dClient).filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(List.of(1, 2), 1);
    }

    @Test
    public void getEquipeVendasBySupervisorId_lancaIntegracaoException_seIdNaoInformado() {
        when(equipeVendaD2dClient.getEquipeVendaBySupervisorId(anyInt()))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo id é obrigatório.\",\"field\":id]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasBySupervisorId(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void getEquipeVendasBySupervisorId_lancaIntegracaoException_seApiNaoDisponivel() {
        when(equipeVendaD2dClient.getEquipeVendaBySupervisorId(anyInt()))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasBySupervisorId(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveLancarIntegracaoException_quandoClientRetornarErro() {
        when(equipeVendaD2dClient.getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt()))
            .thenThrow(new FeignBadResponseWrapper(400, new HttpHeaders(), null));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt()))
            .withMessage("#029 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveLancarIntegracaoException_quandoApiNaoDisponivel() {
        when(equipeVendaD2dClient.getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt()))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt()))
            .withMessage("#029 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(equipeVendaD2dClient, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveRetornarListaSubCanaisId_quandoClientRetornarSemErro() {
        when(equipeVendaD2dClient.getSubCanaisDaEquipeVendaD2dByUsuarioId(123456)).thenReturn(List.of(1, 3));

        assertThat(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(123456))
            .hasSize(2)
            .containsExactly(1, 3);

        verify(equipeVendaD2dClient, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(123456));
    }

    @Test
    public void getUsuariosDaEquipe_deveRetornarListaIdsDeUsuarioDaEquipe_quandoUsuarioTiverEquipe() {
        when(equipeVendaD2dClient.getUsuariosDaEquipe(anyMap()))
            .thenReturn(List.of(umSelectResponse(100),
                umSelectResponse(111),
                umSelectResponse(104),
                umSelectResponse(115)));

        var captor = ArgumentCaptor.forClass(Map.class);

        assertThat(equipeVendaD2dService.getUsuariosDaEquipe(List.of(123456)))
            .hasSize(4)
            .containsExactly(100, 111, 104, 115);

        verify(equipeVendaD2dClient, times(1)).getUsuariosDaEquipe(captor.capture());

        var capturedMap = captor.getValue();

        assertThat(capturedMap.get("equipeVendaIds")).isEqualTo(List.of(123456));
        assertThat(capturedMap.get("ativo")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void getUsuariosDse_deveRetornarListaVazia_quandoHouverErroNaApi() {
        doThrow(RuntimeException.class)
            .when(equipeVendaD2dClient)
            .getUsuariosDaEquipe(anyMap());

        assertThat(equipeVendaD2dService.getUsuariosDaEquipe(anyList()))
            .isEqualTo(List.of());

        verify(equipeVendaD2dClient).getUsuariosDaEquipe(anyMap());
    }

    private SelectResponse umSelectResponse(int id) {
        return SelectResponse.builder()
            .value(id)
            .label("usuario " + id)
            .build();
    }

    private EquipeVendaSupervisorDto umaEquipeVendaSupervisorDto(Integer id) {
        return EquipeVendaSupervisorDto.builder()
            .id(id)
            .descricao("descricao")
            .canalVenda("AGENTE AUTORIZADO")
            .supervisorNome("nome supervisor")
            .build();
    }

    private EquipeVendaUsuarioResponse umaEquipeVendaUsuarioResponse(Integer id) {
        return EquipeVendaUsuarioResponse.builder()
            .id(id)
            .usuarioNome("nome usuario")
            .cargoNome("AGENTE AUTORIZADO GERENTE")
            .equipeVendaId(1)
            .usuarioId(1)
            .build();
    }
}
