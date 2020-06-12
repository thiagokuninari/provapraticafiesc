package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
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
import java.util.stream.StreamSupport;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CidadeRepository repository;

    private Supplier<BooleanBuilder> predicateCidadesPermitidas = () ->
            new CidadePredicate().filtrarPermitidos(autenticacaoService.getUsuarioAutenticado()).build();

    public List<UsuarioCidadeDto> getAllByRegionalId(Integer regionalId) {
        return UsuarioCidadeDto.parse(
                repository.findAllByRegionalId(
                        regionalId,
                        predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        return UsuarioCidadeDto.parse(
                repository.findAllBySubClusterId(
                        subClusterId,
                        predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllBySubClustersId(List<Integer> subClustersId) {
        return UsuarioCidadeDto.parse(
                repository.findAllBySubClustersId(
                        subClustersId,
                        predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        return UsuarioCidadeDto.parse(
                repository.findAllByGrupoId(
                        grupoId,
                        predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        return UsuarioCidadeDto.parse(
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

    public List<CidadeResponse> getCidadesByUfsId(List<Integer> ufsId) {
        var sort = new Sort(ASC, "id");
        var predicate = new CidadePredicate().comEstadosId(ufsId).build();

        return StreamSupport
                .stream(repository.findAll(predicate, sort).spliterator(), false)
                .map(CidadeResponse::parse)
                .collect(Collectors.toList());
    }
}
