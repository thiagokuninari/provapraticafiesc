package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.RelatorioLoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("api/relatorio-login-logout")
public class RelatorioLoginLogoutController {

    @Autowired
    private RelatorioLoginLogoutService service;

    @GetMapping("hoje")
    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(
        PageRequest pageRequest,
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId) {
        return service.getLoginsLogoutsDeHoje(pageRequest, canal, agenteAutorizadoId);
    }

    @GetMapping("csv")
    public void getCsv(
        @Validated RelatorioLoginLogoutCsvFiltro filtro,
        HttpServletResponse response,
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId) {
        service.getCsv(filtro, response, canal, agenteAutorizadoId);
    }

    @GetMapping("colaboradores")
    public List<UsuarioNomeResponse> getColaboradores(
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId) {
        return service.getColaboradores(canal, agenteAutorizadoId);
    }
}
