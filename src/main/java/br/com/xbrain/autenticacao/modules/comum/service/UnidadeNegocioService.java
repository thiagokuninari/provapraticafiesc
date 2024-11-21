package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.predicate.UnidadeNegocioPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<SelectResponse> findWithoutXbrain() {
        return repository.findAll()
            .stream()
            .filter(unidadeNegocio -> unidadeNegocio.getCodigo() != CodigoUnidadeNegocio.XBRAIN)
            .map(unidadeNegocio -> new SelectResponse(unidadeNegocio.getId(), unidadeNegocio.getNome()))
            .collect(Collectors.toList());
    }
}
