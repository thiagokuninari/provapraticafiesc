package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.predicate.GrupoPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<GrupoDto> getAllByRegionalId(Integer regionalId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        GrupoPredicate predicate = new GrupoPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.findAllByRegionalId(regionalId, predicate.build())
                .stream()
                .map(GrupoDto::objectToDto)
                .collect(Collectors.toList());
    }

    public List<GrupoDto> getAllAtiva() {
        return repository.findBySituacao(ESituacao.A, new Sort("nome"))
                .stream()
                .map(GrupoDto::objectToDto)
                .collect(Collectors.toList());
    }

}
