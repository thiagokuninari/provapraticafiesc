package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacaoRepository extends CrudRepository<Organizacao, Integer>, OrganizacaoRepositoryCustom {

}
