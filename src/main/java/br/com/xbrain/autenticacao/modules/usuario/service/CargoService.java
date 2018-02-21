package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoService {

    @Autowired
    private CargoRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public Iterable<Cargo> getAll(Integer operacaoId) {
        CargoPredicate predicate = new CargoPredicate();
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        predicate.comNivel(operacaoId);
        predicate.filtrarPermitidos(usuarioAutenticado);

        return repository.findAll(predicate.build());
    }

}
