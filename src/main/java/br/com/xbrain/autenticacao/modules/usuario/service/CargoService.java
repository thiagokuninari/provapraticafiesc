package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

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

    public Cargo findByUsuarioId(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getCargo)
                .orElse(null);
    }

    public Cargo findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<Cargo> getAll(PageRequest pageRequest, CargoFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public Cargo save(Cargo cargo) {
        return repository.save(cargo);
    }

    public Cargo update(Cargo cargo) {
        Cargo cargoToUpdate = repository.findById(cargo.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        copyProperties(cargo, cargoToUpdate);

        return repository.save(cargoToUpdate);
    }

    public Cargo situacao(CargoRequest cargoRequest) {
        Cargo cargoToUpdate = repository.findById(cargoRequest.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        cargoToUpdate.setSituacao(cargoRequest.getSituacao());

        return repository.save(cargoToUpdate);
    }
}
