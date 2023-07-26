package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.predicate.UnidadeNegocioPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeNegocioService {

    private final UnidadeNegocioRepository repository;
    private final AutenticacaoService autenticacaoService;

    public List<UnidadeNegocio> getAll() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        return repository.findAll(
                new UnidadeNegocioPredicate()
                        .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
                        .build());
    }
}
