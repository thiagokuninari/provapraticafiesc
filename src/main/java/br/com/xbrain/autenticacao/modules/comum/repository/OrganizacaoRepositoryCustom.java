package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface OrganizacaoRepositoryCustom {

    List<Organizacao> findByPredicate(Predicate predicate);
}
