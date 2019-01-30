package br.com.xbrain.autenticacao.modules.solicitacaoramal.timer;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SolicitacaoRamalTimer {

    private static final String EVERY_HOUR_OF_THE_DAY = "0 0 */1 * * *";
    private static final String TIME_ZONE = "America/Sao_Paulo";

    @Autowired
    private SolicitacaoRamalService solicitacaoService;

    @Scheduled(cron = EVERY_HOUR_OF_THE_DAY, zone = TIME_ZONE)
    public void enviarEmailDeNotificacaoParaSolicitacaoRamal() {
        solicitacaoService.enviadorDeEmailParaSolicitacoesQueVaoExpirar();
    }
}
