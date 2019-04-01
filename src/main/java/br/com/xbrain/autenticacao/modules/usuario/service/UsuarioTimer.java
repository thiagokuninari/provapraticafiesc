package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class UsuarioTimer {

    //@Autowired
    //private UsuarioService service;

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    //private static final String EVERY_DAY_AT_TWO_AM = "0 0 2 * * *";
    private static final String TIME_ZONE = "America/Sao_Paulo";
    @Autowired
    private AutenticacaoService autenticacaoService;

    //@Scheduled(cron = EVERY_DAY_AT_TWO_AM)
    public void inativarUsuariosSemAcesso() {
        //service.inativarUsuariosSemAcesso();
    }

    @Transactional
    @Async
    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT, zone = TIME_ZONE)
    public void deslogarTodosOsUsuarios() {
        autenticacaoService.logoutAllUsers();
    }

}
