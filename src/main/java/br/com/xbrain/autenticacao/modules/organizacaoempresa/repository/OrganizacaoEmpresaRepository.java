package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
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

    List<OrganizacaoEmpresa> findAllByNivelIdAndSituacao(Integer nivelId, ESituacaoOrganizacaoEmpresa situacao);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCnpjAndIdNot(String cnpj, Integer id);

    boolean existsByNomeAndIdNot(String nome, Integer id);

    boolean existsByCnpj(String cnpj);
}
