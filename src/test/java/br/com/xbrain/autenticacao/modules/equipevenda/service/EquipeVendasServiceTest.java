package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import feign.RetryableException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EquipeVendasServiceTest {

    @InjectMocks
    private EquipeVendaD2dService equipeVendaD2dService;
    @Mock
    private EquipeVendaD2dClient equipeVendaD2dClient;

    @Test
    public void getEquipeVendasBySupervisorId_lancaIntegracaoException_seIdNaoInformado() {
        when(equipeVendaD2dClient.getEquipeVendaBySupervisorId(anyInt()))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo id é obrigatório.\",\"field\":id]"));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasBySupervisorId(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void getEquipeVendasBySupervisorId_lancaIntegracaoException_seApiNaoDisponivel() {
        when(equipeVendaD2dClient.getEquipeVendaBySupervisorId(anyInt()))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        Assertions.assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> equipeVendaD2dService.getEquipeVendasBySupervisorId(anyInt()))
            .withMessage("#006 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

}
