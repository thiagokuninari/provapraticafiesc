package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface OrganizacaoEmpresaRepositoryCustom {

    List<OrganizacaoEmpresa> findByPredicate(Predicate predicate);

}
