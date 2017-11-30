package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CidadeRepository extends PagingAndSortingRepository<Cidade, Integer>,
        CidadeRepositoryCustom {

    Iterable<Cidade> findCidadeByUfId(Integer idUf, Sort sort);

    Iterable<Cidade> findBySubCluster(Integer subClusterId);

    Iterable<Cidade> findByRegional(Integer regionalId);

    Iterable<Cidade> findByGrupo(Integer grupoId);

    Iterable<Cidade> findByCluster(Integer clusterId);

    @Modifying
    @Query("update Cidade c set c.usuarioAprovadorMso.id = ?1 where c.id = ?2")
    void updateUsuarioAprovador(Integer usuarioId, Integer cidadeId);
}
