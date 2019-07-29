package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoEquipeResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR
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
    private final UsuarioService usuarioService;
    private final CargoService cargoService;
    private final UsuarioRepository usuarioRepository;

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaDistribuicao(Integer usuarioId,
                                                                                              Integer agenteAutorizadoId) {
        var cargoDoUsuario = cargoService.findByUsuarioId(usuarioId);

        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId)
                .stream()
                .map(UsuarioAgenteAutorizadoEquipeResponse::getId)
                .collect(Collectors.toList());

        var usuariosHibridos = obterUsuariosHibridosDoAa(usuariosDoAa);

        var vendedoresDoMesmoCanal = List.<UsuarioAgenteAutorizadoAgendamentoResponse>of();

        if (isVendedor(cargoDoUsuario)) {
            vendedoresDoMesmoCanal = obterVendedoresDoMesmoCanalSemSupervisores(agenteAutorizadoId, usuarioId, usuariosHibridos);
        }

        var usuariosHibridosValidos = filtrarSupervisoresSemPermissaoDeVenda(usuariosHibridos);

        return Stream.concat(usuariosHibridosValidos.stream(), vendedoresDoMesmoCanal.stream())
                .filter(u -> !u.isUsuarioSolicitante(usuarioId))
                .distinct()
                .collect(Collectors.toList());
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

    private List<Usuario> obterUsuariosHibridosDoAa(List<Integer> usuariosDoAa) {
        return usuarioRepository.getUsuariosFilter(new UsuarioPredicate()
                .comIds(usuariosDoAa)
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

    public List<UsuarioAgendamentoResponse> recuperarUsuariosDisponiveisParaDistribuicao(Integer agenteAutorizadoId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var isUsuarioSupervisor = isSupervisor(usuarioAutenticado.getCargoCodigo());

        var usuarios = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId);

        if (isUsuarioSupervisor) {
            return getVendedoresSupervisionados(usuarioAutenticado.getId(), usuarios);
        }

        return usuarios.stream()
                .map(u -> new UsuarioAgendamentoResponse(u.getId(), u.getNome()))
                .collect(Collectors.toList());
    }

    private List<UsuarioAgendamentoResponse> getVendedoresSupervisionados(int supervisorId,
                                                                          List<UsuarioAgenteAutorizadoEquipeResponse> usuarios) {
        var equipesSupervisionadas = equipeVendasService.getEquipesPorSupervisor(supervisorId)
                .stream()
                .map(EquipeVendasSupervisionadasResponse::getId)
                .collect(Collectors.toList());

        return usuarios.stream()
                .filter(u -> equipesSupervisionadas.contains(u.getEquipeVendaId()))
                .map(u -> new UsuarioAgendamentoResponse(u.getId(), u.getNome()))
                .collect(Collectors.toList());
    }
}
