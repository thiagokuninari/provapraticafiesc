package br.com.xbrain.autenticacao.modules.agendador.enums;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.rabbit.AgendadorSender;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.util.concurrent.CompletableFuture.runAsync;

@AllArgsConstructor
public enum EAgendador {

    AUT_22789_01("Remover caches de estrutura por agente autorizado.") {
        @Override
        public void executar(AgendadorService service, AgendadorMqDto mqDto, AgendadorSender agendadorSender) {
            runAsync(() -> {
                service.flushCacheEstruturasAas();
                enviarParaFila(mqDto, agendadorSender);
            }).exceptionally(ex -> {
                setarErroEEnviarParaFila(ex, mqDto, agendadorSender);
                return null;
            });
        }
    },
    AUT_22789_02("Remover caches de feriados.") {
        @Override
        public void executar(AgendadorService service, AgendadorMqDto mqDto, AgendadorSender agendadorSender) {
            runAsync(() -> {
                service.clearCacheFeriados();
                enviarParaFila(mqDto, agendadorSender);
            }).exceptionally(ex -> {
                setarErroEEnviarParaFila(ex, mqDto, agendadorSender);
                return null;
            });
        }
    };

    private final String descricao;

    public abstract void executar(AgendadorService service, AgendadorMqDto mqDto, AgendadorSender agendadorSender);

    public static EAgendador convertFrom(String value) {
        return Arrays.stream(EAgendador.values())
            .filter(agendador -> agendador.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new ValidacaoException("Agendador não encontrado."));
    }

    private static void enviarParaFila(AgendadorMqDto mqDto, AgendadorSender agendadorSender) {
        mqDto.setDataFimExecucao(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        agendadorSender.send(mqDto);
    }

    private static void setarErroEEnviarParaFila(Throwable ex, AgendadorMqDto mqDto, AgendadorSender agendadorSender) {
        mqDto.setErro(ex.getMessage());
        enviarParaFila(mqDto, agendadorSender);
    }
}

