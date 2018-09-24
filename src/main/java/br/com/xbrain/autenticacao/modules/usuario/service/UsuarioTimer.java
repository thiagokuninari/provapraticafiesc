package br.com.xbrain.autenticacao.modules.usuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioTimer {
    
    @Autowired
    private UsuarioService service;

    //private static final String EVERY_DAY_AT_TWO_AM = "0 0 2 * * *";

    //@Scheduled(cron = EVERY_DAY_AT_TWO_AM)
    public void inativarUsuariosSemAcesso() {
        //service.inativarUsuariosSemAcesso();
    }

}
