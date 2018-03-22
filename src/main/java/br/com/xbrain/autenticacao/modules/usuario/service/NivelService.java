package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.NivelPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Nivel> getAll() {
        NivelPredicate predicate = new NivelPredicate();
        predicate.ativo();
        return nivelRepository.getAll(predicate.build());
    }

    public List<Nivel> getAllByPermitidos() {
        NivelPredicate predicate = new NivelPredicate();
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        predicate.filtrarPermitidos(usuarioAutenticado);
        if (!usuarioAutenticado.isXbrain()) {
            predicate.withoutXbrain();
        }
        return nivelRepository.getAllByPermitidos(predicate.build());
    }
}
