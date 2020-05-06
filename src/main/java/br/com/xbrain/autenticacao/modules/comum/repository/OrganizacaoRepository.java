package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizacaoRepository extends CrudRepository<Organizacao, Integer> {

    List<Organizacao> findAll();

    List<Organizacao> findAllByNiveisIdIn(Integer nivelId);

    Optional<Organizacao> findById(Integer id);
}
