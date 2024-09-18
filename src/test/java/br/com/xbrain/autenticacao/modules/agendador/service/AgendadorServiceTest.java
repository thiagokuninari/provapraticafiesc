package br.com.xbrain.autenticacao.modules.agendador.service;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.enums.EStatusAgendador;
import br.com.xbrain.autenticacao.modules.agendador.rabbit.AgendadorSender;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgendadorServiceTest {

    @InjectMocks
    private AgendadorService agendadorService;
    @Mock
    private AgenteAutorizadoService aaService;
    @Mock
    private FeriadoService feriadoService;
    @Mock
    private AgendadorSender agendadorSender;

    @Test
    public void flushCacheEstruturasAas_deveChamarAaService_quandoSolicitado() {
        agendadorService.flushCacheEstruturasAas(new AgendadorMqDto());

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(aaService).flushCacheEstruturasAas();
                verify(agendadorSender).send(any(AgendadorMqDto.class));
            });
    }

    @Test
    public void flushCacheEstruturasAas_deveSetarErroEEnviarParaFila_casoErroNaService() {
        doThrow(new RuntimeException("Erro ao limpar dados"))
            .when(aaService)
            .flushCacheEstruturasAas();

        var dto = new AgendadorMqDto();
        agendadorService.flushCacheEstruturasAas(dto);

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(dto.getErro())
                .isEqualTo("java.lang.RuntimeException: Erro ao limpar dados"));

        verify(agendadorSender).send(any(AgendadorMqDto.class));
    }

    @Test
    public void clearCacheFeriados_deveChamarFeriadoService_quandoSolicitado() {
        agendadorService.clearCacheFeriados(new AgendadorMqDto());

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(feriadoService).flushCacheFeriados();
                verify(agendadorSender).send(any(AgendadorMqDto.class));
            });
    }

    @Test
    public void clearCacheFeriados_deveSetarErroEEnviarParaFila_casoErroNaService() {
        doThrow(new RuntimeException("Erro ao limpar dados"))
            .when(feriadoService)
            .flushCacheFeriados();

        var dto = new AgendadorMqDto();
        agendadorService.clearCacheFeriados(dto);

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(dto.getErro())
                .isEqualTo("java.lang.RuntimeException: Erro ao limpar dados"));

        verify(agendadorSender).send(any(AgendadorMqDto.class));
    }

    @Test
    public void setarErroEEnviarParaFila_deveSetarStatusErroNoDtoEEnviarParaFila() {
        var dto = new AgendadorMqDto();

        agendadorService.setarErroEEnviarParaFila(new ValidacaoException("erro"), dto);

        assertEquals(dto.getStatus(), EStatusAgendador.FALHA);
        verify(agendadorSender).send(dto);
    }

    @Test
    public void setarStatusEmProcessoEEnviarParaFila_deveSetarStutusEmProcessoNoDtoEEnviarParaFila() {
        var dto = new AgendadorMqDto();

        agendadorService.setarStatusEmProcessoEEnviarParaFila(dto);

        assertEquals(dto.getStatus(), EStatusAgendador.EM_PROCESSO);
        verify(agendadorSender).send(dto);
    }
}

