package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.permissao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Getter
    @Autowired
    private CidadeRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public Cidade findByUfNomeAndCidadeNome(String uf, String cidade) {
        CidadePredicate predicate = new CidadePredicate();
        predicate.comNome(cidade);
        predicate.comUf(uf);
        return repository
                .findByPredicate(predicate.build())
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }
}
