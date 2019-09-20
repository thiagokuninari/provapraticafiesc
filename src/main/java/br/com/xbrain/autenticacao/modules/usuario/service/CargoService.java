package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Cargo não encontrado.");

    @Autowired
    private CargoRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CargoSuperiorRepository cargoSuperiorRepository;

    public List<Cargo> getPermitidosPorNivel(Integer nivelId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return repository.findAll(
                new CargoPredicate()
                        .comNivel(nivelId)
                        .filtrarPermitidos(
                                usuarioAutenticado,
                                cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()))
                        .build());
    }

    public List<Cargo> getPermitidosPorNiveis(List<Integer> nivelId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return repository.findAll(
                new CargoPredicate()
                        .comNiveis(nivelId)
                        .filtrarPermitidos(
                                usuarioAutenticado,
                                cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()))
                        .build());
    }

    public Cargo findByUsuarioId(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getCargo)
                .orElse(null);
    }

    public Cargo findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }
}
