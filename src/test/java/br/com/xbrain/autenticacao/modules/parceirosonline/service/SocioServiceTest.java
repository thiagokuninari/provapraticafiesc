package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SocioServiceTest {

    @InjectMocks
    private SocioService service;
    @Mock
    private SocioClient client;

    @Test
    public void findSocioPrincipalByAaId_deveRetornarListaSocioResponse_quandoSolicitado() {
        when(client.findSocioPrincipalByAaId(1))
            .thenReturn(umSocioResponse());

        assertThat(service.findSocioPrincipalByAaId(1))
            .extracting("id", "cpf", "nome")
            .containsExactly(1, "007.008.989-09", "NOME SOCIO");

        verify(client).findSocioPrincipalByAaId(1);
    }

    @Test
    public void findSocioPrincipalByAaId_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(client)
            .findSocioPrincipalByAaId(1);

        assertThatThrownBy(() -> service.findSocioPrincipalByAaId(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#007 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(client).findSocioPrincipalByAaId(1);
    }

    @Test
    public void findSocioPrincipalByAaId_deveLancarIntegracaoException_quandoErroNaApi() {
        doThrow(new HystrixBadRequestException("Bad Request"))
            .when(client)
            .findSocioPrincipalByAaId(1);

        assertThatThrownBy(() -> service.findSocioPrincipalByAaId(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).findSocioPrincipalByAaId(1);
    }

    private SocioResponse umSocioResponse() {
        return SocioResponse.builder()
            .id(1)
            .cpf("007.008.989-09")
            .nome("NOME SOCIO")
            .build();
    }
}
