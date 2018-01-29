package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.predicate.ClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClusterService {

    @Autowired
    private ClusterRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<ClusterDto> getAllByGrupoId(Integer grupoId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        ClusterPredicate predicate = new ClusterPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.findAllByGrupoId(grupoId, predicate.build())
                .stream()
                .map(ClusterDto::objectToDto)
                .collect(Collectors.toList());
    }

    public List<ClusterDto> getAllAtivo() {
        return repository.findBySituacao(ESituacao.A, new Sort("nome"))
                .stream()
                .map(ClusterDto::objectToDto)
                .collect(Collectors.toList());
    }

}