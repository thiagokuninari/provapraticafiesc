package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SolicitacaoRamalHistoricoRepository
        extends CrudRepository<SolicitacaoRamalHistorico, Integer>, SolicitacaoRamalHistoricoRepositoryCustom {

    List<SolicitacaoRamalHistorico> findAllBySolicitacaoRamalId(Integer solicitacaoId);

}
