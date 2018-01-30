package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.predicate.SubClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.SubClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubClusterService {

    @Autowired
    private SubClusterRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<SubClusterDto> getAllByClusterId(Integer clusterId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        SubClusterPredicate predicate = new SubClusterPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.findAllByClusterId(clusterId, predicate.build())
                .stream()
                .map(SubClusterDto::objectToDto)
                .collect(Collectors.toList());
    }

    public List<SubClusterDto> getAllAtivos() {
        return repository.findBySituacao(ESituacao.A, new Sort("nome"))
                .stream()
                .map(SubClusterDto::objectToDto)
                .collect(Collectors.toList());
    }

}
