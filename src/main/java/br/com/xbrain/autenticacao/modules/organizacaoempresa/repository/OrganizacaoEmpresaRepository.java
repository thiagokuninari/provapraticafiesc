package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface OrganizacaoEmpresaRepository extends PagingAndSortingRepository<OrganizacaoEmpresa, Integer>,
    QueryDslPredicateExecutor<OrganizacaoEmpresa> {

    Optional<OrganizacaoEmpresa> findById(Integer id);

    boolean existsByRazaoSocialIgnoreCase(String razaoSocial);

    boolean existsByCnpj(String cnpj);

    boolean existsByRazaoSocialAndCnpjAndIdNot(String razaoSocial, String cnpj, Integer id);
}
