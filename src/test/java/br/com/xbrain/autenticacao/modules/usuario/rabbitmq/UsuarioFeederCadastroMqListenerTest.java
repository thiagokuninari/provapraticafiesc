package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFeederMqDto;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioFeederCadastroMqListenerTest {

    @InjectMocks
    private UsuarioFeederCadastroMqListener mqSender;
    @Mock
    private UsuarioService usuarioService;

    @Test
    public void salvarUsuarioFeeder_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioFeederMqDto();
        mqSender.salvarUsuarioFeeder(dto);
        verify(usuarioService).salvarUsuarioFeeder(dto);
    }

    @Test
    public void salvarUsuarioFeeder_deveLancarLogDeErro_quandoHouverErro() {
        var logger = (Logger) getLogger(UsuarioFeederCadastroMqListener.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);
        var dto = new UsuarioFeederMqDto();

        doThrow(RuntimeException.class).when(usuarioService).salvarUsuarioFeeder(dto);

        mqSender.salvarUsuarioFeeder(dto);

        assertEquals("Erro ao processar fila do cadastro dos usuarios Feeder",
            listAppender.list.get(0).getMessage());
    }
}
