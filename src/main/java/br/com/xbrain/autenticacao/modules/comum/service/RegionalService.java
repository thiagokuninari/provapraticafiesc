package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionalService {

    @Autowired
    private RegionalRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Regional> getAll() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        RegionalPredicate predicate = new RegionalPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.getAll(predicate.build());
    }

}
