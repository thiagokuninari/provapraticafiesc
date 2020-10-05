package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Cargo> getPermitidosPorNivelECanaisPermitidos(Integer nivelId, Collection<ECanal> canais) {
        return getPermitidosPorNivel(nivelId).stream()
            .filter(cargo -> ObjectUtils.isEmpty(canais) || canais.stream().anyMatch(cargo::hasPermissaoSobreOCanal))
            .collect(Collectors.toList());
    }

    public List<Cargo> getPermitidosPorNivel(Integer nivelId) {
        var predicate = new CargoPredicate().comNivel(nivelId);
        filtrarPermitidos(predicate);

        return repository.findAll(predicate.build());
    }

    private void filtrarPermitidos(CargoPredicate predicate) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            predicate.comId(cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()));
        }
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
        var cargoToUpdate = repository.findById(cargo.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        copyProperties(cargo, cargoToUpdate);

        return repository.save(cargoToUpdate);
    }

    public Cargo situacao(CargoRequest cargoRequest) {
        var cargoToUpdate = repository.findById(cargoRequest.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        cargoToUpdate.setSituacao(cargoRequest.getSituacao());

        return repository.save(cargoToUpdate);
    }
}
