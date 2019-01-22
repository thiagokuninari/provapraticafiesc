package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SolicitacaoRamalRepository extends CrudRepository<SolicitacaoRamal, Integer> {

    Page<SolicitacaoRamal> findAllByUsuarioId(Pageable pageable, Integer id);
}
