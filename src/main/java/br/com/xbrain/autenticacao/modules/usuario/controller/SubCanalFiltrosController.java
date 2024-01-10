package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/sub-canais/filtros")
public class SubCanalFiltrosController {

    @GetMapping("canais")
    public List<SelectResponse> getTipoCanalFiltros() {
        return Arrays.stream(ETipoCanal.values())
            .map(tipoCanal -> SelectResponse.of(tipoCanal.name(), tipoCanal.getDescricao().toUpperCase()))
            .collect(Collectors.toList());
    }

    @GetMapping("situacoes")
    public List<SelectResponse> getSubCanalSituacaoFiltros() {
        return ESituacao.getOnlyAtivoInativo()
            .stream()
            .map(situacao -> SelectResponse.of(situacao.name(), situacao.getDescricao().toUpperCase()))
            .collect(Collectors.toList());
    }
}
