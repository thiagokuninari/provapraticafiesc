package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.predicate.UnidadeNegocioPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
public class UnidadeNegocioService {

    @Getter
    @Autowired
    private UnidadeNegocioRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public Iterable<UnidadeNegocio> findWithoutXbrain() {
        return repository.findByNomeIsNot("Xbrain", new Sort(ASC, "nome"));
    }

    public List<UnidadeNegocio> getAll() {
        UnidadeNegocioPredicate predicate = new UnidadeNegocioPredicate();
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        predicate.filtrarPermitidos(usuarioAutenticado);
        if (!usuarioAutenticado.isXbrain()) {
            predicate.withoutXbrain();
        }

        return repository.findAll(predicate.build());
    }
}