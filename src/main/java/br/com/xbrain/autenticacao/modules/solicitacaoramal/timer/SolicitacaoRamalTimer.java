package br.com.xbrain.autenticacao.modules.solicitacaoramal.timer;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class SolicitacaoRamalTimer {

    private static final String EVERY_HOUR_OF_THE_DAY = "0 0 */1 * * *";
    private static final String TIME_ZONE = "America/Sao_Paulo";

    @Autowired
    private SolicitacaoRamalService solicitacaoService;

    @Transactional
    @Scheduled(cron = EVERY_HOUR_OF_THE_DAY, zone = TIME_ZONE)
    public void enviarEmailDeNotificacaoParaSolicitacaoRamal() {
        log.info("Iniciando fluxo para envio de emails de solicitações ramal que vão expirar");
        solicitacaoService.enviarEmailSolicitacoesQueVaoExpirar();
        log.info("Encerrando fluxo para envio de emails de solicitações ramal que vão expirar");
    }
}
