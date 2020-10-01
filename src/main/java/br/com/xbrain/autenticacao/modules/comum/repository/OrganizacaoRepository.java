package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizacaoRepository extends CrudRepository<Organizacao, Integer>, OrganizacaoRepositoryCustom {

    List<Organizacao> findAll();

    List<Organizacao> findAllByNiveisIdIn(Integer nivelId);

    Optional<Organizacao> findById(Integer id);
}
