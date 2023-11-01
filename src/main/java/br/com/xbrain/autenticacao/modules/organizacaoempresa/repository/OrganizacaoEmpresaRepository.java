package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizacaoEmpresaRepository extends PagingAndSortingRepository<OrganizacaoEmpresa, Integer>,
    QueryDslPredicateExecutor<OrganizacaoEmpresa>, CrudRepository<OrganizacaoEmpresa, Integer>,
    OrganizacaoEmpresaRepositoryCustom {

    Optional<OrganizacaoEmpresa> findById(Integer id);

    List<OrganizacaoEmpresa> findAllByNivelId(Integer nivelId);

    List<OrganizacaoEmpresa> findAll(Predicate predicate);

    boolean existsByNomeAndNivelId(String nome, Integer nivelId);

    boolean existsByDescricaoAndNivelId(String codigo, Integer nivelId);

    boolean existsByNomeAndNivelIdAndIdNot(String nome, Integer nivelId, Integer id);

    boolean existsByDescricaoAndNivelIdAndIdNot(String codigo, Integer nivelId, Integer id);

    List<OrganizacaoEmpresa> findAllAtivosByNivelIdInAndSituacao(List<Integer> nivelId, ESituacaoOrganizacaoEmpresa situacao);

    boolean existsByNomeAndSituacao(String nome, ESituacaoOrganizacaoEmpresa situacao);
}
