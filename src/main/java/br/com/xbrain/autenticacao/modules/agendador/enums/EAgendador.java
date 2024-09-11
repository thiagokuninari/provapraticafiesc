package br.com.xbrain.autenticacao.modules.agendador.enums;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum EAgendador {

    AUT_22789_01("Remover caches de estrutura por agente autorizado.") {
        @Override
        public void executar(AgendadorService service, AgendadorMqDto mqDto) {
            service.flushCacheEstruturasAas(mqDto);
        }
    },
    AUT_22789_02("Remover caches de feriados.") {
        @Override
        public void executar(AgendadorService service, AgendadorMqDto mqDto) {
            service.clearCacheFeriados(mqDto);
        }
    };

    private final String descricao;

    public abstract void executar(AgendadorService service, AgendadorMqDto mqDto);

    public static EAgendador convertFrom(String value) {
        return Arrays.stream(EAgendador.values())
            .filter(agendador -> agendador.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new ValidacaoException("Agendador não encontrado."));
    }
}

