package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CidadeRepository extends PagingAndSortingRepository<Cidade, Integer>, CidadeRepositoryCustom {

    List<Cidade> findCidadeByUfId(Integer idUf, Sort sort);

    List<Cidade> findBySubCluster(Integer subClusterId);

    @Modifying
    @Query("update Cidade c set c.usuarioAprovadorMso.id = ?1 where c.id = ?2")
    void updateUsuarioAprovador(Integer usuarioId, Integer cidadeId);

    List<Cidade> findAllByNetUno(Eboolean netUno);

    List<Cidade> findCidadeByNomeLike(String cidade);

    @Query(value = "select * from Cidade c where c.codigo_ibge = ? and c.fk_cidade IS NULL", nativeQuery = true)
    Optional<Cidade> findCidadeByCodigoIbge(String codigoIbge);

    List<Cidade> findAllByUfIdInOrderByNome(List<Integer> estadosIds);
}
