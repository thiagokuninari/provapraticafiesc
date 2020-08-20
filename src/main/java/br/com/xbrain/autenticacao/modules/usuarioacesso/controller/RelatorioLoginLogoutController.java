package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutListagemFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.RelatorioLoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/relatorio-login-logout")
public class RelatorioLoginLogoutController {

    @Autowired
    private RelatorioLoginLogoutService service;

    @GetMapping("hoje")
    public List<LoginLogoutResponse> getLoginsLogoutsDeHoje(RelatorioLoginLogoutListagemFiltro filtro) {
        return service.getLoginsLogoutsDeHoje(filtro);
    }
}
