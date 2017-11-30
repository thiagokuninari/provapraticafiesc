package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import br.com.xbrain.autenticacao.modules.comum.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public List<GrupoDto> getAtivosPorRegional(Integer regionalId) {
        if (regionalId != null) {
            List<Grupo> grupoList =
                    (List) repository.findBySituacaoAndRegionalId(ESituacao.A, regionalId, new Sort("nome"));

            return getGrupoDtoList(grupoList);
        } else {
            List<Grupo> grupoList = (List) repository.findBySituacao(ESituacao.A, new Sort("nome"));

            return getGrupoDtoList(grupoList);
        }
    }

    public List<GrupoDto> getGrupoDtoList(List<Grupo> grupoList) {
        return grupoList
                .stream()
                .map(c -> new GrupoDto(c.getId(), c.getNome()))
                .collect(Collectors.toList());
    }

}