package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticContetInitializer {

    @Autowired
    private FeriadoService feriadoService;

    @PostConstruct
    public void init() {
        feriadoService.loadFeriados();
    }
}
