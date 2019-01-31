package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import org.springframework.data.repository.CrudRepository;

public interface SolicitacaoRamalHistoricoRepository
        extends CrudRepository<SolicitacaoRamalHistorico, Integer>, SolicitacaoRamalHistoricoRepositoryCustom {

}
