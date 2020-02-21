package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrganizacaoRepository extends CrudRepository<Organizacao, Integer> {

    List<Organizacao> findAll();
}
