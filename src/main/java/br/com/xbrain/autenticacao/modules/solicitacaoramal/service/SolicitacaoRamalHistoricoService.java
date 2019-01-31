package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitacaoRamalHistoricoService {

    @Autowired
    private SolicitacaoRamalHistoricoRepository historicoRepository;

    @Transactional
    public SolicitacaoRamalHistorico save(SolicitacaoRamalHistorico historico) {
        return historicoRepository.save(historico);
    }

}
