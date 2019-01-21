package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SolicitacaoRamalRepository extends CrudRepository<SolicitacaoRamal, Integer> {

    List<SolicitacaoRamal> findAllByUsuarioId(Integer id);
}
