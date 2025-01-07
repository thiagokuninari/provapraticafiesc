package br.com.xbrain.autenticacao.modules.agendador.service;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.enums.EStatusAgendador;
import br.com.xbrain.autenticacao.modules.agendador.rabbit.AgendadorSender;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendadorService {

    private final AgenteAutorizadoService aaService;
    private final FeriadoService feriadoService;
    private final AgendadorSender agendadorSender;

    public void flushCacheEstruturasAas(AgendadorMqDto mqDto) {
        log.info("Removendo caches de estrutura por agente autorizado.");
        runAsync(() -> {
            aaService.flushCacheEstruturasAas();
            setarStatusSucessoEEnviarParaFila(mqDto);
        }).exceptionally(ex -> {
            setarErroEEnviarParaFila(ex.getMessage(), mqDto);
            return null;
        });
        log.info("Finaliza processo de remoção de caches de estrutura por agente autorizado.");
    }

    public void clearCacheFeriados(AgendadorMqDto mqDto) {
        log.info("Removendo cache de feriados.");
        runAsync(() -> {
            feriadoService.flushCacheFeriados();
            setarStatusSucessoEEnviarParaFila(mqDto);
        }).exceptionally(ex -> {
            setarErroEEnviarParaFila(ex.getMessage(), mqDto);
            return null;
        });
        log.info("Finaliza processo de remoção de cache de feriados.");
    }

    public void setarErroEEnviarParaFila(String erro, AgendadorMqDto mqDto) {
        mqDto.setErro(erro);
        mqDto.setStatus(EStatusAgendador.FALHA);
        enviarParaFila(mqDto);
    }

    private void setarStatusSucessoEEnviarParaFila(AgendadorMqDto mqDto) {
        mqDto.setStatus(EStatusAgendador.SUCESSO);
        enviarParaFila(mqDto);
    }

    public void setarStatusEmProcessoEEnviarParaFila(AgendadorMqDto mqDto) {
        mqDto.setStatus(EStatusAgendador.EM_PROCESSO);
        agendadorSender.send(mqDto);
    }

    private void enviarParaFila(AgendadorMqDto mqDto) {
        mqDto.setDataFimExecucao(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        agendadorSender.send(mqDto);
    }
}
