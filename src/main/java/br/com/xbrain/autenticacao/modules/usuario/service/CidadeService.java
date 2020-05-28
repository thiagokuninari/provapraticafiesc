package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CidadeRepository repository;

    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

    private Supplier<BooleanBuilder> predicateCidadesPermitidas = () ->
        new CidadePredicate().filtrarPermitidos(autenticacaoService.getUsuarioAutenticado()).build();

    public List<UsuarioCidadeDto> getAllByRegionalId(Integer regionalId) {
        return UsuarioCidadeDto.of(
            repository.findAllByRegionalId(
                regionalId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        return UsuarioCidadeDto.of(
            repository.findAllBySubClusterId(
                subClusterId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllBySubClustersId(List<Integer> subClustersId) {
        return UsuarioCidadeDto.of(
            repository.findAllBySubClustersId(
                subClustersId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        return UsuarioCidadeDto.of(
            repository.findAllByGrupoId(
                grupoId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        return UsuarioCidadeDto.of(
            repository.findAllByClusterId(clusterId,
                predicateCidadesPermitidas.get()));
    }

    public List<Cidade> getAllCidadeByUf(Integer idUf) {
        return repository.findCidadeByUfId(idUf, new Sort("nome"));
    }

    public List<Cidade> getAllBySubCluster(Integer idSubCluster) {
        return repository.findBySubCluster(idSubCluster);
    }

    public Cidade findByUfNomeAndCidadeNome(String uf, String cidade) {
        return repository
            .findByPredicate(
                new CidadePredicate()
                    .comNome(cidade)
                    .comUf(uf)
                    .build())
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public ClusterizacaoDto getClusterizacao(Integer id) {
        return repository.getClusterizacao(id);
    }

    public List<UsuarioCidadeDto> getAtivosParaComunicados(Integer subclusterId) {
        return Stream.concat(
            agenteAutorizadoService.getCidades(subclusterId).stream(),
            getAllBySubCluster(subclusterId).stream().map(UsuarioCidadeDto::of))
            .filter(distinctByKey(UsuarioCidadeDto::getIdCidade))
            .collect(Collectors.toList());
    }
}
