package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.predicate.ClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto.of;
import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;

@Service
public class ClusterService {

    @Autowired
    private ClusterRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

    public List<ClusterDto> getAllByGrupoId(Integer grupoId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        ClusterPredicate predicate = new ClusterPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.findAllByGrupoId(grupoId, predicate.build())
            .stream()
            .map(ClusterDto::of)
            .collect(Collectors.toList());
    }

    public List<ClusterDto> getAllByGrupoIdAndUsuarioId(Integer grupoId, Integer usuarioId) {
        ClusterPredicate predicate = new ClusterPredicate()
            .filtrarPermitidos(usuarioId);
        return repository.findAllByGrupoId(grupoId, predicate.build())
            .stream()
            .map(ClusterDto::of)
            .collect(Collectors.toList());
    }

    public List<ClusterDto> getAllAtivo() {
        return repository.findBySituacao(ESituacao.A, new Sort("nome"))
            .stream()
            .map(ClusterDto::of)
            .collect(Collectors.toList());
    }

    public ClusterDto findById(Integer clusterId) {
        return of(repository.findById(clusterId)
            .orElseThrow(() -> new ValidacaoException("Cluster não encontrado.")));
    }

    public List<ClusterDto> getAtivosParaComunicados(Integer grupoId) {
        return Stream.concat(
            getAllByGrupoId(grupoId).stream(),
            agenteAutorizadoService.getClusters(grupoId).stream())
            .filter(distinctByKey(ClusterDto::getId))
            .collect(Collectors.toList());
    }
}
