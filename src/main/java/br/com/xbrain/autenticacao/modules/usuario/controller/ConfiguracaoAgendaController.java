package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRealHistoricoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaRealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/configuracoes/agenda")
public class ConfiguracaoAgendaController {

    private final ConfiguracaoAgendaRealService service;

    @GetMapping
    public Page<ConfiguracaoAgendaResponse> buscar(@Valid ConfiguracaoAgendaFiltros filtros, PageRequest pageable) {
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
    public ConfiguracaoAgendaResponse salvar(@RequestBody @Valid ConfiguracaoAgendaRequest request) {
        request.aplicarValidacoes();
        return service.salvar(request);
    }

    @PutMapping("{id}/atualizar")
    public void atualizar(@PathVariable Integer id, @RequestParam Integer qtdHoras) {
        service.atualizar(id, qtdHoras);
    }

    @GetMapping("horas-adicionais")
    public Integer getQtdHorasAdicionaisAgendaByUsuario(@RequestParam(required = false) Integer subcanalId,
                                                        @RequestParam(required = false) Integer aaId) {
        return service.getQtdHorasAdicionaisAgendaByUsuario(subcanalId, aaId);
    }

    @GetMapping("{id}/historico")
    public Page<ConfiguracaoAgendaRealHistoricoResponse> findHistoricoByConfiguracaoId(@PathVariable Integer id,
                                                                                       PageRequest pageable) {
        return service.findHistoricoByConfiguracaoId(id, pageable);
    }
}
