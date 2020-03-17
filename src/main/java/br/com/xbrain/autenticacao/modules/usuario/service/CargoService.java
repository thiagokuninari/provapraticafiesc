package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
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

    @Autowired
    private NivelRepository nivelRepository;

    public List<Cargo> getPermitidosPorNivel(Integer nivelId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return repository.findAll(
            new CargoPredicate()
                .comNivel(nivelId)
                .filtrarPermitidos(
                    usuarioAutenticado,
                    cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()))
                .build());
    }

    public List<Cargo> getPermitidosPorNiveis(List<Integer> niveisIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return repository.findAll(
            new CargoPredicate()
                .comNiveis(niveisIds)
                .filtrarPermitidos(
                    usuarioAutenticado,
                    cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()))
                .build());
    }

    public List<CargoResponse> getPermitidosAosComunicados(List<Integer> niveisIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var cargosIds = cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId());

        return repository.findAll(
            new CargoPredicate()
                .comNiveis(niveisIds)
                .filtrarPermitidos(usuarioAutenticado, cargosIds)
                .ouComCodigos(getCodigosAgenteAutorizado(niveisIds))
                .build())
            .stream()
            .map(CargoResponse::of)
            .collect(Collectors.toList());
    }

    private List<CodigoCargo> getCodigosAgenteAutorizado(List<Integer> niveisIds) {
        return niveisIds.contains(nivelRepository.findByCodigo(CodigoNivel.AGENTE_AUTORIZADO).getId())
            ? List.of(AGENTE_AUTORIZADO_ACEITE,
            AGENTE_AUTORIZADO_SOCIO,
            AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
            AGENTE_AUTORIZADO_SUPERVISOR,
            AGENTE_AUTORIZADO_GERENTE,
            AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS,
            AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
            AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
            AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS_RECEPTIVO,
            AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS_RECEPTIVO,
            AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS_RECEPTIVO,
            AGENTE_AUTORIZADO_VENDEDOR_D2D,
            AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO,
            AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
            AGENTE_AUTORIZADO_BACKOFFICE_D2D,
            AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS,
            AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
            AGENTE_AUTORIZADO_SUPERVISOR_TEMP,
            AGENTE_AUTORIZADO_GERENTE_TEMP,
            AGENTE_AUTORIZADO_VENDEDOR_TEMP,
            AGENTE_AUTORIZADO_BACKOFFICE_TEMP,
            AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TEMP,
            AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN,
            AGENTE_AUTORIZADO_COORDENADOR,
            AGENTE_AUTORIZADO_EMPRESARIO,
            AGENTE_AUTORIZADO_APRENDIZ,
            AGENTE_AUTORIZADO_ASSISTENTE)
            : List.of();
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
