package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeriadoTimer {

    private static final String TODO_DIA_AS_DUAS_DA_MANHA = "0 0 2 ? * * ";
    private static final String UMA_VEZ_POR_ANO_EM_CINCO_DE_DEZEMBRO = "0 0 2 5 12 ? *";
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final FeriadoService feriadoService;
    private final ImportacaoAutomaticaFeriadoService importacaoService;

    @Scheduled(cron = TODO_DIA_AS_DUAS_DA_MANHA, zone = TIME_ZONE)
    public void clearCacheFeriados() {
        feriadoService.flushCacheFeriados();
    }

    @Scheduled(cron = UMA_VEZ_POR_ANO_EM_CINCO_DE_DEZEMBRO, zone = TIME_ZONE)
    public void importarTodosOsFeriados() {
        importacaoService.importarTodosOsFeriadoAnuais();
    }

}
