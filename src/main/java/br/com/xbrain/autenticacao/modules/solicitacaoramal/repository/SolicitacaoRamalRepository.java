package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SolicitacaoRamalRepository extends CrudRepository<SolicitacaoRamal, Integer>, SolicitacaoRamalRepositoryCustom {

    Optional<SolicitacaoRamal> findById(Integer id);

}
