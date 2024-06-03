package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<Cargo> getPermitidosPorNivelECanaisPermitidos(Integer nivelId, Collection<ECanal> canais,
                                                              boolean permiteEditarCompleto) {
        return filtrarPorNivelCanalOuCargoProprio(nivelId, canais, permiteEditarCompleto).stream()
            .filter(cargo -> ObjectUtils.isEmpty(canais) || canais.stream().anyMatch(cargo::hasPermissaoSobreOCanal))
            .collect(Collectors.toList());
    }

    private List<Cargo> filtrarPorNivelCanalOuCargoProprio(Integer nivelId, Collection<ECanal> canais,
                                                           boolean permiteEditarCompleto) {
        var predicate = new CargoPredicate();
        if (canais != null && new ArrayList<>(canais).equals(List.of(ECanal.INTERNET))) {
            predicate.comNivel(nivelId).comCanal(ECanal.INTERNET);
        } else {
            predicate.comNivel(nivelId);
        }
        return permiteEditarCompleto ? getPermitidosPorNivel(predicate) : cargoProprio(predicate, nivelId);
    }

    public List<Cargo> cargoProprio(CargoPredicate cargoPredicate, Integer nivelId) {
        cargoPredicate.comId(getCargosPermitidosParaEditar());
        cargoPredicate.comNivel(nivelId);
        return repository.findAll(cargoPredicate.build());
    }

    public List<Cargo> getPermitidosPorNivel(CargoPredicate cargoPredicate) {
        filtrarPermitidos(cargoPredicate);
        return repository.findAll(cargoPredicate.build());
    }

    private void filtrarPermitidos(CargoPredicate predicate) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (!
            usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            predicate.comId(cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId()));
        }
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

    public List<SelectResponse> getPermitidosAosComunicados(List<Integer> niveisIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var cargosIds = cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId());

        return repository.buscarTodosComNiveis(
                new CargoPredicate()
                    .comNiveis(niveisIds)
                    .filtrarPermitidos(usuarioAutenticado, cargosIds)
                    .ouComCodigos(getCodigosEspeciais(niveisIds, usuarioAutenticado))
                    .build())
            .stream()
            .map(cargo -> SelectResponse.of(cargo.getId(), String.join(" - ",
                cargo.getNome(), cargo.getNivel().getNome())))
            .collect(Collectors.toList());
    }

    private List<CodigoCargo> getCodigosEspeciais(List<Integer> niveisIds, UsuarioAutenticado usuarioAutenticado) {
        if (niveisIds.contains(nivelRepository.findByCodigo(CodigoNivel.AGENTE_AUTORIZADO).getId())) {
            return Stream.of(CodigoCargo.values())
                .filter(this::isAgenteAutorizado)
                .collect(Collectors.toList());
        } else if (usuarioAutenticado.isSupervisorOperacao()) {
            return List.of(CodigoCargo.ASSISTENTE_OPERACAO);
        } else {
            return List.of();
        }
    }

    public boolean isAgenteAutorizado(CodigoCargo codigoCargo) {
        return codigoCargo.name().contains("AGENTE_AUTORIZADO") && !codigoCargo.name().contains("XBRAIN");
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

    private List<Integer> getCargosPermitidosParaEditar() {
        var cargoProprioId = autenticacaoService.getUsuarioAutenticado().getCargoId();
        var cargosPermitidos = cargoSuperiorRepository.getCargosHierarquia(cargoProprioId);
        cargosPermitidos.add(cargoProprioId);

        return cargosPermitidos;
    }

    public List<CodigoCargo> getAllCargos() {
        return Arrays.asList(CodigoCargo.values());
    }
}
