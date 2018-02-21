package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.predicate.DepartamentoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Departamento> getAllByNivelId(Integer nivelId) {
        DepartamentoPredicate predicate = new DepartamentoPredicate();
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        predicate.filtrarPermitidos(usuarioAutenticado);
        predicate.deNivelId(nivelId);
        return repository.findAll(predicate.build());
    }

}
