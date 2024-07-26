package br.com.xbrain.autenticacao.modules.agenteautorizado.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.PermissaoTecnicoIndicadorService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class PermissaoTecnicoIndicadorMqListenerTest {

    @InjectMocks
    private PermissaoTecnicoIndicadorMqListener listener;
    @Mock
    private PermissaoTecnicoIndicadorService service;

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissaoAosUsuarios_quandoSolicitado() {
        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(service, times(1)).atualizarPermissaoTecnicoIndicador(eq(dto));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveLancarLogDeErro_quandoHouverErro() {
        var logger = (Logger) getLogger(PermissaoTecnicoIndicadorMqListener.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);
        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        doThrow(RuntimeException.class).when(service).atualizarPermissaoTecnicoIndicador(dto);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        assertEquals("Erro ao processar fila para atualizar permissão de técnico indicador",
            listAppender.list.get(0).getMessage());
    }
}
