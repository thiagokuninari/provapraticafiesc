package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class CargoService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Cargo não "
            + "encontrado.");

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

    public Page<Cargo> getAll(PageRequest pageRequest, CargoFiltros filtros) {
        CargoPredicate predicate = filtros.toPredicate();
        Page<Cargo> pages = repository.findAll(predicate.build(), pageRequest);
        return pages;
    }

    public Cargo findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Cargo save(Cargo cargo) {
        return repository.save(cargo);
    }

    public Cargo update(Cargo cargo) {
        if (!validaCargoExiste(cargo)) {
            throw new ValidacaoException("Cargo não existente.");
        }

        Cargo cargoToUpdate = repository.findById(cargo.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        BeanUtils.copyProperties(cargo, cargoToUpdate);

        return repository.save(cargoToUpdate);
    }

    public boolean validaCargoExiste(Cargo cargo) {
        return repository.exists(cargo.getId());
    }

}
