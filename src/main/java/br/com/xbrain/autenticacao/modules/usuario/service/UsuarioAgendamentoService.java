package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDisponivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.*;
import static com.google.common.collect.Lists.partition;

@Service
@RequiredArgsConstructor
public class UsuarioAgendamentoService {

    private final UsuarioRepository repository;
    private final CargoService cargoService;
    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;
    private final EquipeVendasService equipeVendasService;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final EquipeVendasUsuarioService equipeVendasUsuarioService;

    @SuppressWarnings("LineLength")
    public List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaDistribuicao(Integer usuarioId,
                                                                                              Integer agenteAutorizadoId,
                                                                                              String tipoContato) {
        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, false);
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var usuariosIds = getUsuarioIds(usuariosDoAa);
        var usuariosHibridos = obterUsuariosHibridosDoAa(usuariosIds, usuarioAutenticado);

        return isUsuarioAutenticadoSupervisor(usuarioAutenticado)
            ? getColaboradoresSupervionados(usuarioId, agenteAutorizadoId, tipoContato, usuarioAutenticado, usuariosDoAa, usuariosHibridos)
            : getColaboradoresVendas(usuarioId, agenteAutorizadoId, tipoContato, usuariosDoAa, usuariosHibridos);
    }

    public List<UsuarioDistribuicaoResponse> getUsuariosParaDistribuicaoByEquipeVendaId(Integer equipeVendaId) {
        return equipeVendasUsuarioService.getAll(getFiltros(equipeVendaId))
            .stream()
            .map(UsuarioDistribuicaoResponse::of)
            .collect(Collectors.toList());
    }

    public List<UsuarioDisponivelResponse> recuperarUsuariosDisponiveisParaDistribuicao(Integer agenteAutorizadoId) {
        var usuariosPol = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, true);
        var usuarios = getUsuariosAtivosAutenticacao(usuariosPol);
        popularEquipeVendasId(usuarios);

        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        if (isUsuarioAutenticadoSupervisor(usuarioAutenticado)) {
            var supervisorComPermissaoVenda = filtrarSupervisoresSemPermissaoDeVenda(List.of(usuarioAutenticado.getUsuario()))
                .stream()
                .map(usuario ->
                    new UsuarioDisponivelResponse(usuario.getId(), usuario.getNome(), isUsuarioHibrido(usuario.getId())))
                .collect(Collectors.toList());

            var vendedoresSupervisionados = getVendedoresSupervisionados(usuarioAutenticado.getUsuario().getId(), usuarios)
                .stream()
                .map(usuario ->
                    new UsuarioDisponivelResponse(usuario.getId(), usuario.getNome(), isUsuarioHibrido(usuario.getId())));

            return Stream.concat(supervisorComPermissaoVenda.stream(), vendedoresSupervisionados)
                .collect(Collectors.toList());
        }

        return usuarios.stream()
            .map(usuario -> new UsuarioDisponivelResponse(usuario.getId(), usuario.getNome(), isUsuarioHibrido(usuario.getId())))
            .collect(Collectors.toList());
    }

    private boolean isUsuarioHibrido(Integer id) {
        var usuario = usuarioService.getUsuarioById(id);
        return usuario != null && isCargoHibrido(usuario.getCargoCodigo());
    }

    @SuppressWarnings("LineLength")
    private List<UsuarioAgenteAutorizadoAgendamentoResponse> getColaboradoresSupervionados(Integer usuarioId,
                                                                                           Integer agenteAutorizadoId,
                                                                                           String tipoContato,
                                                                                           UsuarioAutenticado usuarioAutenticado,
                                                                                           List<UsuarioAgenteAutorizadoResponse> usuariosDoAa,
                                                                                           List<Usuario> usuariosHibridos) {

        var supervisorComPermissaoVenda = filtrarSupervisoresSemPermissaoDeVenda(
            Collections.singletonList(usuarioAutenticado.getUsuario()));

        var usuariosSupervisionados = getVendedoresSupervisionados(usuarioAutenticado.getId(), usuariosDoAa)
            .stream()
            .map(UsuarioAgendamentoResponse::getId)
            .collect(Collectors.toList());

        return filtrarUsuariosParaDistribuicao(usuarioId, agenteAutorizadoId, tipoContato, usuariosDoAa, usuariosHibridos, supervisorComPermissaoVenda)
            .stream()
            .filter(u -> usuariosSupervisionados.contains(u.getId())
                || Objects.equals(u.getId(), usuarioAutenticado.getId()))
            .distinct()
            .collect(Collectors.toList());
    }

    @SuppressWarnings("LineLength")
    private List<UsuarioAgenteAutorizadoAgendamentoResponse> getColaboradoresVendas(Integer usuarioId,
                                                                                    Integer agenteAutorizadoId,
                                                                                    String tipoContato,
                                                                                    List<UsuarioAgenteAutorizadoResponse> usuariosDoAa,
                                                                                    List<Usuario> usuariosHibridos) {

        var usuariosHibridosValidos = filtrarSupervisoresSemPermissaoDeVenda(usuariosHibridos);

        return filtrarUsuariosParaDistribuicao(usuarioId, agenteAutorizadoId, tipoContato, usuariosDoAa, usuariosHibridos, usuariosHibridosValidos);
    }

    @SuppressWarnings("LineLength")
    private List<UsuarioAgenteAutorizadoAgendamentoResponse> filtrarUsuariosParaDistribuicao(Integer usuarioId,
                                                                                             Integer agenteAutorizadoId,
                                                                                             String tipoContato,
                                                                                             List<UsuarioAgenteAutorizadoResponse> usuariosDoAa,
                                                                                             List<Usuario> usuariosHibridos,
                                                                                             List<UsuarioAgenteAutorizadoAgendamentoResponse> request) {

        var vendedoresDoMesmoCanal = getVendedoresDoMesmoCanal(usuarioId, agenteAutorizadoId, usuariosHibridos);
        var vendedoresDoAa = getVendedoresDoAa(tipoContato, usuariosDoAa);
        var listaVendedores = Stream.concat(vendedoresDoMesmoCanal.stream(), vendedoresDoAa.stream())
            .collect(Collectors.toList());

        return Stream.concat(request.stream(), listaVendedores.stream())
            .filter(u -> !u.isUsuarioSolicitante(usuarioId))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> getVendedoresDoMesmoCanal(Integer usuarioId,
                                                                                       Integer agenteAutorizadoId,
                                                                                       List<Usuario> usuariosHibridos) {
        return Optional.ofNullable(cargoService.findByUsuarioId(usuarioId))
            .filter(this::isVendedor)
            .map(u -> obterVendedoresDoMesmoCanalSemSupervisores(agenteAutorizadoId, usuarioId, usuariosHibridos))
            .orElseGet(List::of);
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> obterVendedoresDoMesmoCanalSemSupervisores(
        Integer agenteAutorizadoId,
        Integer usuarioId,
        List<Usuario> usuariosHibridos) {

        var usuariosIds = usuariosHibridos.stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());

        return agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(agenteAutorizadoId, usuarioId)
            .stream()
            .filter(u -> !usuariosIds.contains(u.getId()))
            .collect(Collectors.toList());
    }

    private List<Usuario> obterUsuariosHibridosDoAa(List<Integer> usuariosDoAa, UsuarioAutenticado usuarioAutenticado) {
        Optional.ofNullable(usuarioAutenticado)
            .filter(u -> this.isSupervisor(u.getCargoCodigo()))
            .ifPresent(u -> usuariosDoAa.add(u.getId()));

        return getUsuariosById(usuariosDoAa)
            .stream()
            .filter(u -> isCargoHibrido(u.getCargoCodigo()))
            .collect(Collectors.toList());
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> filtrarSupervisoresSemPermissaoDeVenda(List<Usuario> usuarios) {
        return usuarios.stream()
            .filter(this::isSupervisorComPermissaoDeVenda)
            .map(UsuarioAgenteAutorizadoAgendamentoResponse::of)
            .collect(Collectors.toList());
    }

    private boolean isSupervisorComPermissaoDeVenda(Usuario usuario) {
        return !isSupervisor(usuario.getCargoCodigo()) || hasPermissaoVenda(usuarioService.findPermissoesByUsuario(usuario));
    }

    private boolean hasPermissaoVenda(UsuarioPermissaoResponse usuarioPermissaoResponse) {
        return Stream.concat(
                usuarioPermissaoResponse.getPermissoesCargoDepartamento()
                    .stream()
                    .map(CargoDepartamentoFuncionalidadeResponse::getFuncionalidadeRole),
                usuarioPermissaoResponse.getPermissoesEspeciais()
                    .stream()
                    .map(FuncionalidadeResponse::getRole))
            .anyMatch(this::isPermissaoVenda);
    }

    private boolean isPermissaoVenda(String role) {
        return PERMISSOES_DE_VENDA.contains(role);
    }

    private boolean isSupervisor(CodigoCargo codigoCargo) {
        return CARGOS_SUPERVISOR.contains(codigoCargo);
    }

    private boolean isVendedor(Cargo cargoDoUsuario) {
        return Objects.nonNull(cargoDoUsuario)
            && Objects.equals(cargoDoUsuario.getNivel().getCodigo(), CodigoNivel.AGENTE_AUTORIZADO)
            && !isCargoHibrido(cargoDoUsuario.getCodigo());
    }

    private boolean isCargoHibrido(CodigoCargo codigoCargo) {
        return CARGOS_HIBRIDOS_PERMITIDOS.contains(codigoCargo);
    }

    private List<UsuarioAgenteAutorizadoResponse> getUsuariosAtivosAutenticacao(
        List<UsuarioAgenteAutorizadoResponse> usuarios) {

        var usuariosAut = partition(getUsuarioIds(usuarios), QTD_MAX_IN_NO_ORACLE)
            .stream()
            .map(usuarioService::getUsuariosAtivosByIds)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        return usuarios.stream()
            .filter(usuario -> usuariosAut.contains(usuario.getId()))
            .collect(Collectors.toList());
    }

    private List<Integer> getUsuarioIds(List<UsuarioAgenteAutorizadoResponse> usuarios) {
        return usuarios.stream().map(UsuarioAgenteAutorizadoResponse::getId)
            .collect(Collectors.toList());
    }

    private void popularEquipeVendasId(List<UsuarioAgenteAutorizadoResponse> usuarios) {
        var usuarioEquipes = equipeVendasService.getUsuarioEEquipeByUsuarioIds(getUsuarioIds(usuarios));

        usuarios.forEach(usuario -> usuario.setEquipeVendaId(usuarioEquipes.getOrDefault(usuario.getId(), null)));
    }

    private Map<String, Object> getFiltros(Integer equipeVendaId) {
        return Map.of("ativo", true, "equipeVendaId", equipeVendaId);
    }

    private List<UsuarioAgendamentoResponse> getVendedoresSupervisionados(int supervisorId,
                                                                          List<UsuarioAgenteAutorizadoResponse> usuarios) {
        var equipesSupervisionadas = equipeVendasService.getEquipesPorSupervisor(supervisorId)
            .stream()
            .map(EquipeVendasSupervisionadasResponse::getId)
            .collect(Collectors.toList());

        return usuarios.stream()
            .filter(usuario -> equipesSupervisionadas.contains(usuario.getEquipeVendaId()))
            .map(usuario -> new UsuarioAgendamentoResponse(usuario.getId(), usuario.getNome()))
            .collect(Collectors.toList());
    }

    private boolean isUsuarioAutenticadoSupervisor(UsuarioAutenticado usuarioAutenticado) {
        return Optional.ofNullable(usuarioAutenticado)
            .map(UsuarioAutenticado::getCargoCodigo)
            .map(this::isSupervisor)
            .orElse(false);
    }

    @SuppressWarnings("LineLength")
    private List<UsuarioAgenteAutorizadoAgendamentoResponse> getVendedoresDoAa(String tipoContato,
                                                                               List<UsuarioAgenteAutorizadoResponse> usuariosDoAa) {
        var usuariosIds = getUsuarioIds(usuariosDoAa);

        return getUsuariosById(usuariosIds).stream()
            .filter(usuario -> isPermissaoVendaValida(usuario, tipoContato))
            .map(UsuarioAgenteAutorizadoAgendamentoResponse::of)
            .collect(Collectors.toList());
    }

    private boolean isPermissaoVendaValida(Usuario usuario, String tipoContato) {
        var permissaoUsuario = usuarioService.findPermissoesByUsuario(usuario);
        var permissaoValida = validarTipoContato(tipoContato);

        return Stream.concat(
                permissaoUsuario.getPermissoesCargoDepartamento().stream()
                    .map(CargoDepartamentoFuncionalidadeResponse::getFuncionalidadeRole),
                permissaoUsuario.getPermissoesEspeciais().stream()
                    .map(FuncionalidadeResponse::getRole))
            .anyMatch(permissaoValida::equalsIgnoreCase);
    }

    private List<Usuario> getUsuariosById(List<Integer> ids) {
        return repository.getUsuariosFilter(
            new UsuarioPredicate()
                .comIds(ids)
                .build());
    }

    private String validarTipoContato(String tipoContato) {
        switch (tipoContato) {
            case "PRESENCIAL":
                return "VDS_TABULACAO_MANUAL";
            case "CLICK_TO_CALL":
                return "VDS_TABULACAO_CLICKTOCALL";
            case "DISCADORA":
                return "VDS_TABULACAO_DISCADORA";
            default:
                return "";
        }
    }

}
