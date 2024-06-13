package br.com.xbrain.autenticacao.modules.feeder.rabbitmq;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.feeder.dto.AgenteAutorizadoPermissaoFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.dto.SituacaoAlteracaoUsuarioFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class FeederMqListenerTest {

    @InjectMocks
    private FeederMqListener listener;
    @Mock
    private FeederService service;

    @Test
    public void atualizarPermissaoFeeder_deveAtualizarPermissaoFeeder_quandoSolicitado() {
        listener.atualizarPermissaoFeeder(umAgenteAutorizadoPermissaoFeederDto());

        verify(service, times(1))
            .atualizarPermissaoFeeder(eq(umAgenteAutorizadoPermissaoFeederDto()));
    }

    @Test
    public void atualizarPermissaoFeeder_deveLancarLogDeErro_quandoHouverErro() {
        var logger = (Logger) getLogger(FeederMqListener.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        doThrow(RuntimeException.class).when(service).atualizarPermissaoFeeder(umAgenteAutorizadoPermissaoFeederDto());

        listener.atualizarPermissaoFeeder(umAgenteAutorizadoPermissaoFeederDto());

        assertEquals("Erro ao processar fila de mensagem de atualizar permissões de agente autorizado para Feeder.",
            listAppender.list.get(0).getMessage());
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveAlterarSituacaoUsuarioFeeder_quandoSolicitado() {
        listener.alterarSituacaoUsuarioFeeder(umaSituacaoAlteracaoUsuarioFeederDto());

        verify(service, times(1))
            .alterarSituacaoUsuarioFeeder(eq(umaSituacaoAlteracaoUsuarioFeederDto()));
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveLancarLogDeErro_quandoHouverErro() {
        var logger = (Logger) getLogger(FeederMqListener.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        doThrow(RuntimeException.class).when(service).alterarSituacaoUsuarioFeeder(umaSituacaoAlteracaoUsuarioFeederDto());

        listener.alterarSituacaoUsuarioFeeder(umaSituacaoAlteracaoUsuarioFeederDto());

        assertEquals("Erro ao processar fila de mensagem de alterar a situação de usuário Feeder.",
            listAppender.list.get(0).getMessage());
    }

    @Test
    public void limparCpfEAlterarEmailFeeder_deveLimparCpfEAlterarEmailFeeder_quandoSolicitado() {
        listener.limparCpfEAlterarEmailFeeder(1);

        verify(service, times(1))
            .limparCpfEAlterarEmailUsuarioFeeder(eq(1));
    }

    @Test
    public void limparCpfEAlterarEmailFeeder_deveLancarLogDeErro_quandoHouverErro() {
        var logger = (Logger) getLogger(FeederMqListener.class);
        var listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        logger.addAppender(listAppender);

        doThrow(RuntimeException.class).when(service).limparCpfEAlterarEmailUsuarioFeeder(1);

        listener.limparCpfEAlterarEmailFeeder(1);

        assertEquals("Erro ao processar fila de mensagem de limpar cpf e alterar o e-mail de usuário Feeder.",
            listAppender.list.get(0).getMessage());
    }

    private AgenteAutorizadoPermissaoFeederDto umAgenteAutorizadoPermissaoFeederDto() {
        return AgenteAutorizadoPermissaoFeederDto.builder()
            .agenteAutorizadoId(1)
            .usuarioCadastroId(1)
            .feeder(ETipoFeeder.RESIDENCIAL)
            .build();
    }

    private SituacaoAlteracaoUsuarioFeederDto umaSituacaoAlteracaoUsuarioFeederDto() {
        return SituacaoAlteracaoUsuarioFeederDto.builder()
            .usuarioId(1)
            .situacaoAlterada(ESituacao.R)
            .usuarioAlteracaoId(1)
            .build();
    }
}
