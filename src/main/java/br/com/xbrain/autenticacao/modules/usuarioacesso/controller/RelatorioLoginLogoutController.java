package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.RelatorioLoginLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/relatorio-login-logout")
public class RelatorioLoginLogoutController {

    private final RelatorioLoginLogoutService service;

    @GetMapping("hoje")
    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(
        PageRequest pageRequest,
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId,
        @RequestParam(required = false) Integer subCanalId) {
        return service.getLoginsLogoutsDeHoje(pageRequest, canal, agenteAutorizadoId, subCanalId);
    }

    @PostMapping("entre-datas")
    public List<LoginLogoutResponse> getLoginsLogoutsEntreDatas(@RequestBody @Validated RelatorioLoginLogoutRequest request) {
        request.validarDatas();
        return service.buscarAcessosEntreDatasPorUsuarios(request);
    }

    @GetMapping("csv")
    public void getCsv(
        @Validated RelatorioLoginLogoutCsvFiltro filtro,
        HttpServletResponse response,
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId,
        @RequestParam(required = false) Integer subCanalId) {
        service.getCsv(filtro, response, canal, agenteAutorizadoId, subCanalId);
    }

    @GetMapping("colaboradores")
    public List<UsuarioNomeResponse> getColaboradores(
        @RequestHeader("X-Usuario-Canal") ECanal canal,
        @RequestParam(required = false) Integer agenteAutorizadoId,
        @RequestParam(required = false) Integer subCanalId) {
        return service.getColaboradores(canal, agenteAutorizadoId, subCanalId);
    }
}
