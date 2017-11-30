package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
public class UnidadeNegocioService {

    @Getter
    @Autowired
    private UnidadeNegocioRepository repository;

    public Iterable<UnidadeNegocio> findWithoutXbrain() {
        return repository.findByNomeIsNot("Xbrain", new Sort(ASC, "nome"));
    }
}