package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutListagemFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.RelatorioLoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/relatorio-login-logout")
public class RelatorioLoginLogoutController {

    @Autowired
    private RelatorioLoginLogoutService service;

    @GetMapping("hoje")
    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(
        @Validated RelatorioLoginLogoutListagemFiltro filtro,
        PageRequest pageRequest) {
        return service.getLoginsLogoutsDeHoje(filtro, pageRequest);
    }

    @GetMapping("csv")
    public void getCsv(
        @Validated RelatorioLoginLogoutCsvFiltro filtro,
        HttpServletResponse response) {
        service.getCsv(filtro, response);
    }
}
