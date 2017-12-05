package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "api/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public List<GrupoDto> getAtivosPorRegional(Integer regionalId) {
        return regionalId != null
                ? GrupoDto.converterParaListaDto(
                repository.findBySituacaoAndRegionalId(ESituacao.A, regionalId, new Sort("nome")))
                : GrupoDto.converterParaListaDto(
                repository.findBySituacao(ESituacao.A, new Sort("nome")));
    }
}