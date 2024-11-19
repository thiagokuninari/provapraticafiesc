package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
public class CargoService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Cargo não encontrado.");
    private static final String EX_CODIGO_CARGO_EXISTENTE = "Já existe um cargo ativo com o mesmo código.";

    private final CargoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService autenticacaoService;
    private final CargoSuperiorRepository cargoSuperiorRepository;
    private final NivelRepository nivelRepository;

    public List<Cargo> getPermitidosPorNivelECanaisPermitidos(Integer nivelId, Collection<ECanal> canais,
                                                              boolean permiteEditarCompleto) {
        return filtrarPorNivelCanalOuCargoProprio(nivelId, canais, permiteEditarCompleto).stream()
            .filter(cargo -> ObjectUtils.isEmpty(canais) || canais.stream().anyMatch(cargo::hasPermissaoSobreOCanal))
            .collect(Collectors.toList());
    }

    private List<Cargo> filtrarPorNivelCanalOuCargoProprio(Integer nivelId, Collection<ECanal> canais,
                                                           boolean permiteEditarCompleto) {
        var predicate = new CargoPredicate();
        adicionarFiltroComNivelECanal(predicate, nivelId, canais);
        adicionarFiltroComId(predicate, permiteEditarCompleto);

        return repository.findAll(predicate.build());
    }

    private void adicionarFiltroComNivelECanal(CargoPredicate predicate, Integer nivelId, Collection<ECanal> canais) {
        predicate.comNivel(nivelId);
        if (canais != null && new ArrayList<>(canais).equals(List.of(ECanal.INTERNET))) {
            predicate.comCanal(ECanal.INTERNET);
        }
    }

    private void adicionarFiltroComId(CargoPredicate predicate, boolean permiteEditarCompleto) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        if (!permiteEditarCompleto || !usuarioAutenticado.isVisualizaGeral()) {
            var cargoProprioId = usuarioAutenticado.getCargoId();
            var cargosPermitidos = cargoSuperiorRepository.getCargosHierarquia(cargoProprioId);

            if (!permiteEditarCompleto) {
                cargosPermitidos.add(cargoProprioId);
            }

            predicate.comId(cargosPermitidos);
        }
    }

    public List<Cargo> getPermitidosPorNivel(CargoPredicate cargoPredicate) {
        filtrarPermitidos(cargoPredicate);
        return repository.findAll(cargoPredicate.build());
    }

    private void filtrarPermitidos(CargoPredicate predicate) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
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
        validarCodigoExistente(cargo.getCodigo());

        return repository.save(cargo);
    }

    public Cargo update(Cargo cargo) {
        var cargoToUpdate = repository.findById(cargo.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        copyProperties(cargo, cargoToUpdate);

        validarCodigoExistente(cargoToUpdate.getCodigo(), cargoToUpdate.getId());

        return repository.save(cargoToUpdate);
    }

    public Cargo situacao(CargoRequest cargoRequest) {
        var cargoToUpdate = repository.findById(cargoRequest.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        cargoToUpdate.setSituacao(cargoRequest.getSituacao());

        if (cargoRequest.getSituacao() == ESituacao.A) {
            validarCodigoExistente(cargoRequest.getCodigo(), cargoRequest.getId());
        }

        return repository.save(cargoToUpdate);
    }

    public List<SelectResponse> getAllCargos() {
        return Stream.of(CodigoCargo.values())
            .map(codigoCargo -> new SelectResponse(codigoCargo, codigoCargo.getDescricao()))
            .collect(Collectors.toList());
    }

    private void validarCodigoExistente(CodigoCargo codigo) {
        if (repository.existsByCodigoAndSituacao(codigo, ESituacao.A)) {
            throw new ValidacaoException(EX_CODIGO_CARGO_EXISTENTE);
        }
    }

    private void validarCodigoExistente(CodigoCargo codigo, Integer id) {
        if (repository.existsByCodigoAndSituacaoAndIdNot(codigo, ESituacao.A, id)) {
            throw new ValidacaoException(EX_CODIGO_CARGO_EXISTENTE);
        }
    }
}
