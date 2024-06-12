package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasServiceTest.umaEquipeVendaDto;
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
    public void getEquipeVendas_deveRetornarListaEquipeVendaDto_quandoUsuarioTiverEquipe() {
        when(equipeVendaD2dClient.getUsuario(any()))
            .thenReturn(List.of(umaEquipeVendaDto(),
                umaEquipeVendaDto()));

        assertThat(equipeVendaD2dService.getEquipeVendas(1))
            .extracting("id", "descricao", "canalVenda")
            .containsExactly(tuple(1, "Equipe Teste 2", "TELEVENDAS"),
                tuple(1, "Equipe Teste 2", "TELEVENDAS"));

        verify(equipeVendaD2dClient).getUsuario(any());
    }

    @Test
    public void getEquipeVendas_deveLancarIntegracaoException_seIdNaoInformado() {
        when(equipeVendaD2dClient.getUsuario(any()))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo id é obrigatório.\",\"field\":id]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendas(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void getEquipeVendasComSupervisor_deveRetornarListaEquipeVendaDto_quandoUsuarioTiverEquipe() {
        when(equipeVendaD2dClient.getUsuarioComSupervisor(any()))
            .thenReturn(List.of(umaEquipeVendaSupervisorDto(1),
                umaEquipeVendaSupervisorDto(2)));

        assertThat(equipeVendaD2dService.getEquipeVendasComSupervisor(1))
            .extracting("id", "descricao", "canalVenda", "supervisorNome")
            .containsExactly(tuple(1, "descricao", "AGENTE AUTORIZADO", "nome supervisor"),
                tuple(2, "descricao", "AGENTE AUTORIZADO", "nome supervisor"));

        verify(equipeVendaD2dClient).getUsuarioComSupervisor(any());
    }

    @Test
    public void getEquipeVendasComSupervisor_deveLancarIntegracaoException_seIdNaoInformado() {
        when(equipeVendaD2dClient.getUsuarioComSupervisor(any()))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo id é obrigatório.\",\"field\":id]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasComSupervisor(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
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
        when(equipeVendaD2dClient.getUsuariosDaEquipe(any()))
            .thenReturn(List.of(umUsuarioResponse(100),
                umUsuarioResponse(111),
                umUsuarioResponse(104),
                umUsuarioResponse(115)));

        assertThat(equipeVendaD2dService.getUsuariosDaEquipe(List.of(123456)))
            .hasSize(4)
            .containsExactly(100, 111, 104, 115);

        verify(equipeVendaD2dClient, times(1)).getUsuariosDaEquipe(any());
    }

    @Test
    public void getUsuariosDse_deveRetornarListaVazia_quandoHouverErroNaApi() {
        doThrow(RuntimeException.class)
            .when(equipeVendaD2dClient)
            .getUsuariosDaEquipe(any());

        assertThat(equipeVendaD2dService.getUsuariosDaEquipe(any()))
            .isEqualTo(List.of());

        verify(equipeVendaD2dClient, times(1)).getUsuariosDaEquipe(any());
    }

    private SelectResponse umUsuarioResponse(int id) {
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
}
