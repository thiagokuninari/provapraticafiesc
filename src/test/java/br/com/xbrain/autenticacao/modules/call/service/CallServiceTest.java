package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.modules.call.dto.ConfiguracaoTelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.SuporteVendasBkoRequest;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallServiceTest {

    @InjectMocks
    private CallService callService;
    @Mock
    private CallClient callClient;

    @Test
    public void obterNomeTelefoniaPorId_deveRetornarTelefoniaResponse_quandoNaoHouverProblemaComClient() {
        var telefoniaResponse = TelefoniaResponse.builder()
            .nome("telefonia 1")
            .id(1)
            .build();

        when(callClient.obterNomeTelefoniaPorId(1)).thenReturn(telefoniaResponse);
        assertThatCode(() -> callService.obterNomeTelefoniaPorId(1))
            .doesNotThrowAnyException();

        verify(callClient).obterNomeTelefoniaPorId(1);
    }

    @Test
    public void obterNomeTelefoniaPorId_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).obterNomeTelefoniaPorId(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.obterNomeTelefoniaPorId(1));

        verify(callClient).obterNomeTelefoniaPorId(1);
    }

    @Test
    public void obterNomeTelefoniaPorId_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).obterNomeTelefoniaPorId(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.obterNomeTelefoniaPorId(1));

        verify(callClient).obterNomeTelefoniaPorId(1);
    }

    @Test
    public void obterRamaisParaCanal_deveRetornarListaDeRamalResponse_quandoSolicitado() {
        var ramalResponse = new RamalResponse();
        ramalResponse.setRamal("ramal 1");
        ramalResponse.setId(1);

        when(callClient.obterRamaisParaCanal(AGENTE_AUTORIZADO, 1)).thenReturn(List.of(ramalResponse));

        assertThat(callService.obterRamaisParaCanal(AGENTE_AUTORIZADO, 1))
            .extracting("ramal", "id")
            .containsExactly(tuple("ramal 1", 1));

        verify(callClient).obterRamaisParaCanal(AGENTE_AUTORIZADO, 1);
    }

    @Test
    public void obterRamaisParaCanal_deveRetornarListaVazia_quandoNaoHouverRamaisParaOCanal() {
        when(callClient.obterRamaisParaCanal(D2D_PROPRIO, 1)).thenReturn(List.of());

        assertThat(callService.obterRamaisParaCanal(D2D_PROPRIO, 1))
            .isEmpty();

        verify(callClient).obterRamaisParaCanal(D2D_PROPRIO, 1);
    }

    @Test
    public void obterRamaisParaCanal_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).obterRamaisParaCanal(AGENTE_AUTORIZADO, 1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.obterRamaisParaCanal(AGENTE_AUTORIZADO, 1));

        verify(callClient).obterRamaisParaCanal(AGENTE_AUTORIZADO, 1);
    }

    @Test
    public void obterRamaisParaCanal_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).obterRamaisParaCanal(D2D_PROPRIO, 1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.obterRamaisParaCanal(D2D_PROPRIO, 1));

        verify(callClient).obterRamaisParaCanal(D2D_PROPRIO, 1);
    }

    @Test
    public void cleanCacheFeriadosTelefonia_deveLimparCache_quandoSolicitado() {
        doNothing().when(callClient).cleanCacheFeriadosTelefonia();

        assertThatCode(() -> callService.cleanCacheFeriadosTelefonia())
            .doesNotThrowAnyException();

        verify(callClient).cleanCacheFeriadosTelefonia();
    }

    @Test
    public void cleanCacheFeriadosTelefonia_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).cleanCacheFeriadosTelefonia();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.cleanCacheFeriadosTelefonia());

        verify(callClient).cleanCacheFeriadosTelefonia();
    }

    @Test
    public void cleanCacheFeriadosTelefonia_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).cleanCacheFeriadosTelefonia();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.cleanCacheFeriadosTelefonia());

        verify(callClient).cleanCacheFeriadosTelefonia();
    }

    @Test
    public void desvincularRamaisDaDiscadoraAtivoProprio_deveDesvincullarRamais_quandoSolicitado() {
        doNothing().when(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);

        assertThatCode(() -> callService.desvincularRamaisDaDiscadoraAtivoProprio(1, 2))
            .doesNotThrowAnyException();

        verify(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);
    }

    @Test
    public void desvincularRamaisDaDiscadoraAtivoProprio_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularRamaisDaDiscadoraAtivoProprio(1, 2));

        verify(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);
    }

    @Test
    public void desvincularRamaisDaDiscadoraAtivoProprio_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularRamaisDaDiscadoraAtivoProprio(1, 2));

        verify(callClient).desvincularRamaisDaDiscadoraAtivoProprio(1, 2);
    }

    @Test
    public void cleanCacheableSiteAtivoProprio_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).cleanCacheableSiteAtivoProprio();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.cleanCacheableSiteAtivoProprio());

        verify(callClient).cleanCacheableSiteAtivoProprio();
    }

    @Test
    public void cleanCacheabeSiteAtivoProprio_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).cleanCacheableSiteAtivoProprio();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.cleanCacheableSiteAtivoProprio());

        verify(callClient).cleanCacheableSiteAtivoProprio();
    }

    @Test
    public void consultarStatusUsoRamalByUsuarioAutenticado_deveRetornarTrue_quandoUsuarioEmLigacao() {
        when(callClient.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(true);

        assertThat(callService.consultarStatusUsoRamalByUsuarioAutenticado())
            .isTrue();

        verify(callClient).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void consultarStatusUsoRamalByUsuarioAutenticado_deveRetornarFalse_quandoUsuarioNaoEstiverEmLigacao() {
        when(callClient.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(false);

        assertThat(callService.consultarStatusUsoRamalByUsuarioAutenticado())
            .isFalse();

        verify(callClient).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void consultarStatusUsoRamalByUsuarioAutenticado_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).consultarStatusUsoRamalByUsuarioAutenticado();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.consultarStatusUsoRamalByUsuarioAutenticado());

        verify(callClient).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void consultarStatusUsoRamalByUsuarioAutenticado_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).consultarStatusUsoRamalByUsuarioAutenticado();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.consultarStatusUsoRamalByUsuarioAutenticado());

        verify(callClient).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void liberarRamalUsuarioAutenticado_deveLiberarRamais_quandoSolicitado() {
        doNothing().when(callClient).liberarRamalUsuarioAutenticado();

        assertThatCode(() -> callService.liberarRamalUsuarioAutenticado())
            .doesNotThrowAnyException();

        verify(callClient).liberarRamalUsuarioAutenticado();
    }

    @Test
    public void liberarRamalUsuarioAutenticado_deveLancarException_quandoErroDeConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).liberarRamalUsuarioAutenticado();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.liberarRamalUsuarioAutenticado());

        verify(callClient).liberarRamalUsuarioAutenticado();
    }

    @Test
    public void liberarRamalUsuarioAutenticado_deveLancarException_quandoErroDeRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).liberarRamalUsuarioAutenticado();

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.liberarRamalUsuarioAutenticado());

        verify(callClient).liberarRamalUsuarioAutenticado();
    }

    @Test
    public void getDiscadoras_deveRetornarListaDeResponse_quandoSolicitado() {
        var response = ConfiguracaoTelefoniaResponse.builder()
            .ip("127.0.0.1")
            .id(1)
            .nome("Discadora").build();

        when(callClient.getDiscadoras()).thenReturn(List.of(response));

        assertThat(callService.getDiscadoras())
            .extracting("id", "nome", "ip")
            .containsExactly(tuple(1, "Discadora", "127.0.0.1"));

        verify(callClient).getDiscadoras();
    }

    @Test
    public void salvarConfiguracaoSuporteVendas_naoDeveLancarException_quandoRequisicaoEnviadaComSucesso() {
        var request = SuporteVendasBkoRequest.of(1, "fornecedor");

        doNothing().when(callClient).salvarConfiguracaoSuporteVendas(request);

        assertThatCode(() -> callService.salvarConfiguracaoSuporteVendas(1, "fornecedor"))
            .doesNotThrowAnyException();

        verify(callClient).salvarConfiguracaoSuporteVendas(request);
    }

    @Test
    public void salvarConfiguracaoSuporteVendas_deveLancarException_quandoErroDeConexaoComClient() {
        doThrow(RetryableException.class).when(callClient)
            .salvarConfiguracaoSuporteVendas(any(SuporteVendasBkoRequest.class));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.salvarConfiguracaoSuporteVendas(1,"fornecedor"));

        verify(callClient).salvarConfiguracaoSuporteVendas(any(SuporteVendasBkoRequest.class));
    }

    @Test
    public void salvarConfiguracaoSuporteVendas_deveLancarException_quandoErroDeRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient)
            .salvarConfiguracaoSuporteVendas(any(SuporteVendasBkoRequest.class));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.salvarConfiguracaoSuporteVendas(1,"fornecedor"));

        verify(callClient).salvarConfiguracaoSuporteVendas(any(SuporteVendasBkoRequest.class));
    }

    @Test
    public void atualizarConfiguracaoSuporteVendas_naoDeveLancarException_quandoRequisicaoEnviadaComSucesso() {
        var request = SuporteVendasBkoRequest.of("organizacao");

        doNothing().when(callClient).atualizarConfiguracaoSuporteVendas(1, request);

        assertThatCode(() -> callService.atualizarConfiguracaoSuporteVendas(1, "organizacao"))
            .doesNotThrowAnyException();

        verify(callClient).atualizarConfiguracaoSuporteVendas(1, request);
    }

    @Test
    public void atualizarConfiguaracaoSuporteVendas_deveLancarException_quandoErroConexaoComClient() {
        var request = SuporteVendasBkoRequest.of("organizacao");

        doThrow(RetryableException.class).when(callClient).atualizarConfiguracaoSuporteVendas(1, request);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.atualizarConfiguracaoSuporteVendas(1, "organizacao"));

        verify(callClient).atualizarConfiguracaoSuporteVendas(1, request);
    }

    @Test
    public void atualizarConfiguaracaoSuporteVendas_deveLancarException_quandoErroDeRequisicao() {
        var request = SuporteVendasBkoRequest.of("organizacao");

        doThrow(HystrixBadRequestException.class).when(callClient).atualizarConfiguracaoSuporteVendas(1, request);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.atualizarConfiguracaoSuporteVendas(1, "organizacao"));

        verify(callClient).atualizarConfiguracaoSuporteVendas(1, request);
    }

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveDesvincularDiscadoraERamais_quandoNenhumErroComClient() {
        doNothing().when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatCode(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1))
            .doesNotThrowAnyException();

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1));

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1));

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendas_deveDesvincularAtivarConfiguracao_quandoNenhumErroComClient() {
        doNothing().when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatCode(() -> callService.ativarConfiguracaoSuporteVendas(1))
            .doesNotThrowAnyException();

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendass_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.ativarConfiguracaoSuporteVendas(1));

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendass_deveLancarException_quandoErroRequisicao() {
        doThrow(RetryableException.class).when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.ativarConfiguracaoSuporteVendas(1));

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }
}
