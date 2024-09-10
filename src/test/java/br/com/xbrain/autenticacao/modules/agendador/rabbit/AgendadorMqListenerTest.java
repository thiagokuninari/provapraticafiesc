package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

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
        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("AGD_01");

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> listener.executarAgendador(agendadorMqDto))
            .withMessage("Agendador não encontrado.");
    }
}

