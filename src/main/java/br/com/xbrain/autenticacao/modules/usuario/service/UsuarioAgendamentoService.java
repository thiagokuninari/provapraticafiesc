package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static com.google.common.collect.Lists.partition;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioAgendamentoService {

    private static final List<CodigoCargo> CARGOS_HIBRIDOS_PERMITIDOS = List.of(
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR,
        CodigoCargo.AGENTE_AUTORIZADO_ACEITE
    );
    private static final List<String> PERMISSOES_DE_VENDA = List.of(
        "VDS_TABULACAO_MANUAL",
        "VDS_TABULACAO_DISCADORA",
        "VDS_TABULACAO_CLICKTOCALL",
        "VDS_TABULACAO_PERSONALIZADA"
    );
    private static final List<CodigoCargo> CARGOS_SUPERVISOR = List.of(
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_TEMP
    );

    private final AutenticacaoService autenticacaoService;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final EquipeVendasService equipeVendasService;
    private final EquipeVendasUsuarioService equipeVendasUsuarioService;
    private final UsuarioService usuarioService;
    private final CargoService cargoService;
    private final UsuarioRepository usuarioRepository;

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaDistribuicao(Integer usuarioId,
                                                                                              Integer agenteAutorizadoId,
                                                                                              String tipoContato) {
        return isUsuarioAutenticadoSupervisor()
            ? recuperarUsuariosParaSupervisor(usuarioId, agenteAutorizadoId, tipoContato)
            : recuperarUsuariosParaOutrosUsuarios(usuarioId, agenteAutorizadoId, tipoContato);
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaSupervisor(Integer usuarioId,
                                                                                             Integer agenteAutorizadoId,
                                                                                             String tipoContato) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        var supervisorComPermissaoVenda = filtrarSupervisoresSemPermissaoDeVenda(
            Collections.singletonList(usuarioAutenticado));

        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, false);
        var usuariosSupervisionados = getVendedoresSupervisionados(usuarioAutenticado.getId(), usuariosDoAa)
            .stream()
            .map(UsuarioAgendamentoResponse::getId)
            .collect(Collectors.toList());

        return filtrarUsuariosSolicitante(usuarioId, agenteAutorizadoId, tipoContato, supervisorComPermissaoVenda).stream()
            .filter(u -> usuariosSupervisionados.contains(u.getId())
                || Objects.equals(u.getId(), usuarioAutenticado.getId()))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaOutrosUsuarios(Integer usuarioId,
                                                                                                 Integer agenteAutorizadoId,
                                                                                                 String tipoContato) {

        var usuariosHibridos = obterUsuariosHibridosDoAa(agenteAutorizadoId);
        var usuariosHibridosValidos = filtrarSupervisoresSemPermissaoDeVenda(usuariosHibridos);

        return filtrarUsuariosSolicitante(usuarioId, agenteAutorizadoId, tipoContato, usuariosHibridosValidos);
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> filtrarUsuariosSolicitante(
        Integer usuarioId,
        Integer agenteAutorizadoId,
        String tipoContato,
        List<UsuarioAgenteAutorizadoAgendamentoResponse> request) {

        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, false);
        var usuariosIds = getUsuarioIds(usuariosDoAa);
        var usuariosHibridos = obterUsuariosHibridosDoAa(agenteAutorizadoId);

        var vendedores = obterVendedores(usuarioId, agenteAutorizadoId, tipoContato, usuariosIds, usuariosHibridos);
        return Stream.concat(request.stream(), vendedores.stream())
            .filter(u -> !u.isUsuarioSolicitante(usuarioId))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> obterVendedores(Integer usuarioId,
                                                                             Integer agenteAutorizadoId,
                                                                             String tipoContato,
                                                                             List<Integer> usuariosIds,
                                                                             List<Usuario> usuariosHibridos) {
        var vendedoresDoAa = obterVendedoresDoAa(usuariosIds, tipoContato);
        var vendedoresDoMesmoCanal =
            getVendedoresDoMesmoCanal(usuarioId, agenteAutorizadoId, usuariosHibridos);

        return Stream.concat(vendedoresDoMesmoCanal.stream(), vendedoresDoAa.stream())
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

    private List<Usuario> obterUsuariosHibridosDoAa(Integer agenteAutorizadoId) {
        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, false);
        var usuariosIds = getUsuarioIds(usuariosDoAa);

        Optional.ofNullable(autenticacaoService.getUsuarioAutenticado())
            .filter(u -> this.isSupervisor(u.getCargoCodigo()))
            .ifPresent(u -> usuariosIds.add(u.getId()));

        return usuarioRepository.getUsuariosFilter(new UsuarioPredicate()
                .comIds(usuariosIds)
                .build())
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

    public List<UsuarioAgendamentoResponse> recuperarUsuariosDisponiveisParaDistribuicao(Integer agenteAutorizadoId) {
        var usuariosPol = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, true);
        var usuarios = getUsuariosAtivosAutenticacao(usuariosPol);
        popularEquipeVendasId(usuarios);

        if (isUsuarioAutenticadoSupervisor()) {
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
            var supervisorComPermissaoVenda = filtrarSupervisoresSemPermissaoDeVenda(List.of(usuarioAutenticado))
                .stream()
                .map(usuario -> new UsuarioAgendamentoResponse(usuario.getId(), usuario.getNome()))
                .collect(Collectors.toList());
            var vendedoresSupervisionados = getVendedoresSupervisionados(usuarioAutenticado.getId(), usuarios);

            return Stream.concat(supervisorComPermissaoVenda.stream(), vendedoresSupervisionados.stream())
                .map(usuario -> new UsuarioAgendamentoResponse(usuario.getId(), usuario.getNome()))
                .collect(Collectors.toList());
        }

        return usuarios.stream()
            .map(usuario -> new UsuarioAgendamentoResponse(usuario.getId(), usuario.getNome()))
            .collect(Collectors.toList());
    }

    public List<UsuarioDistribuicaoResponse> getUsuariosParaDistribuicaoByEquipeVendaId(Integer equipeVendaId) {
        return equipeVendasUsuarioService.getAll(getFiltros(equipeVendaId))
            .stream()
            .map(UsuarioDistribuicaoResponse::of)
            .collect(Collectors.toList());
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

    private boolean isUsuarioAutenticadoSupervisor() {
        return Optional.ofNullable(autenticacaoService.getUsuarioAutenticado())
            .map(UsuarioAutenticado::getCargoCodigo)
            .map(this::isSupervisor)
            .orElse(false);
    }

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> obterVendedoresDoAa(
        List<Integer> usuarioIds,
        String tipoContato) {

        return getUsuarioById(usuarioIds).stream()
            .filter(usuario -> validarUsuarioPermissao(usuario, tipoContato))
            .map(UsuarioAgenteAutorizadoAgendamentoResponse::of)
            .collect(Collectors.toList());
    }

    private boolean validarUsuarioPermissao(Usuario usuario, String tipoContato) {
        var permissao = validarTipoContato(tipoContato);

        var permissaoUsuario = usuarioService.findPermissoesByUsuario(usuario);

        return Stream.concat(
                permissaoUsuario.getPermissoesCargoDepartamento()
                    .stream()
                    .map(CargoDepartamentoFuncionalidadeResponse::getFuncionalidadeRole),
                permissaoUsuario.getPermissoesEspeciais()
                    .stream()
                    .map(FuncionalidadeResponse::getRole))
            .anyMatch(permissao::equalsIgnoreCase);
    }

    private List<Usuario> getUsuarioById(List<Integer> ids) {
        return usuarioRepository.getUsuariosFilter(
            new UsuarioPredicate()
                .comIds(ids)
                .build());
    }

    private String validarTipoContato(String tipoContato) {
        return Objects.equals(tipoContato, "PRESENCIAL")
            ? "VDS_TABULACAO_MANUAL"
            : Objects.equals(tipoContato, "CLICK_TO_CALL")
            ? "VDS_TABULACAO_CLICKTOCALL"
            : "VDS_TABULACAO_DISCADORA";
    }

}
