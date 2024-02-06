package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/configuracoes/agenda")
public class ConfiguracaoAgendaController {

    private final ConfiguracaoAgendaService service;

    @GetMapping
    public Page<ConfiguracaoAgendaResponse> buscar(ConfiguracaoAgendaFiltros filtros, PageRequest pageable) {
        return service.findAll(filtros, pageable);
    }

    @PutMapping("{id}/ativar")
    public void ativar(@PathVariable Integer id) {
        service.alterarSituacao(id, ESituacao.A);
    }

    @PutMapping("{id}/inativar")
    public void inativar(@PathVariable Integer id) {
        service.alterarSituacao(id, ESituacao.I);
    }

    @PostMapping
    public ConfiguracaoAgendaResponse salvar(@Valid @RequestBody ConfiguracaoAgendaRequest request) {
        return service.salvar(request);
    }

    @GetMapping("horas-adicionais")
    public Integer getQtdHorasAdicionaisAgendaByUsuario(@RequestParam(required = false) ETipoCanal subcanal) {
        return service.getQtdHorasAdicionaisAgendaByUsuario(subcanal);
    }
}
