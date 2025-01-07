package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.NivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/niveis")
@RequiredArgsConstructor
public class NivelController {

    private final NivelService service;

    @GetMapping
    public Iterable<Nivel> getAll() {
        return service.getAll();
    }

    @GetMapping("codigo/{codigoNivel}")
    public NivelResponse getByCodigo(@PathVariable CodigoNivel codigoNivel) {
        return service.getByCodigo(codigoNivel);
    }

    @GetMapping(value = "/permitidos/{tipoVisualizacao}")
    public Iterable<Nivel> getPermitidos(@PathVariable NivelTipoVisualizacao tipoVisualizacao) {
        return service.getPermitidos(tipoVisualizacao);
    }

    @GetMapping("comunicados")
    public List<Nivel> getNiveisParaComunicados() {
        return service.getPermitidosParaComunicados();
    }

    @GetMapping("organizacao")
    public List<NivelResponse> getNivelParaOrganizacao() {
        return service.getPermitidosParaOrganizacao();
    }

    @GetMapping("configuracoes-tratativas")
    public List<NivelResponse> getNiveisConfiguracoesTratativas() {
        return service.getNiveisConfiguracoesTratativas();
    }
}
