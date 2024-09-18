package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import lombok.SneakyThrows;
import nl.altindag.log.LogCaptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgendadorMqListenerTest {

    @InjectMocks
    private AgendadorMqListener listener;
    @Mock
    private AgendadorService service;
    @Mock
    private AgendadorSender agendadorSender;

    @Test
    @SneakyThrows
    public void executarAgendador_deveExecutarMetodo_quandoSolicitado() {
        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("AUT_22789_01");

        assertThatCode(() -> listener.executarAgendador(agendadorMqDto))
            .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    public void executarAgendador_deveLancarException_quandoNaoEncontrarMetodoParaExecutar() {
        var logger = LogCaptor.forClass(AgendadorMqListener.class);

        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("AGD_01");

        listener.executarAgendador(agendadorMqDto);

        assertThat(logger.getLogs().get(0))
            .isEqualTo("Inicia execução do agendador: AGD_01.");
        assertThat(logger.getLogs().get(1))
            .isEqualTo("Erro ao executar agendador");

        verify(service).setarErroEEnviarParaFila(anyString(), eq(agendadorMqDto));
    }
}

