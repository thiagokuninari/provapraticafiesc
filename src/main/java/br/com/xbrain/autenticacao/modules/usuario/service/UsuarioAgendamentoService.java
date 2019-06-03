package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.mailing.service.TabulacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.DistribuirTabulacoesMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
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
    private final TabulacaoService tabulacaoService;
    private final ColaboradorVendasService colaboradorVendasService;
    private final DistribuirTabulacoesMqSender distribuirTabulacoesMqSender;
    private final UsuarioService usuarioService;
    private final CargoService cargoService;
    private final UsuarioRepository usuarioRepository;

    public List<AgendamentoDistribuicaoListagemResponse> getAgendamentoDistribuicaoDoUsuario(Integer usuarioId) {
        var agendamentos = agruparAgendamentosPorAgenteAutorizado(
                        tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(usuarioId));

        var colaboradorVendas =
                colaboradorVendasService.getEquipeVendasSupervisorDoUsuarioId(usuarioId)
                .orElseGet(EquipeVendasSupervisorResponse::empty);

        return agenteAutorizadoService.getAgentesAutorizadosPermitidos()
                .stream()
                .filter(aaPermitido -> agendamentos.containsKey(aaPermitido.getId()))
                .map(aaPermitido -> getDistribuicaoListagem(
                        usuarioId,
                        colaboradorVendas,
                        aaPermitido,
                        agendamentos.get(aaPermitido.getId())))
                .sorted(Comparator.comparing(AgendamentoDistribuicaoListagemResponse::getCnpjRazaoSocial))
                .collect(Collectors.toList());
    }

    private Map<Integer, Long> agruparAgendamentosPorAgenteAutorizado(List<AgendamentoAgenteAutorizadoResponse> agendamentosAa) {
        return agendamentosAa
                .stream()
                .collect(Collectors.toMap(
                        AgendamentoAgenteAutorizadoResponse::getAgenteAutorizadoId,
                        AgendamentoAgenteAutorizadoResponse::getQuantidadeAgendamentos));
    }

    private AgendamentoDistribuicaoListagemResponse getDistribuicaoListagem(Integer usuarioId,
                                                                            EquipeVendasSupervisorResponse equipeVendasSupervisor,
                                                                            AgenteAutorizadoPermitidoResponse aaPermitido,
                                                                            Long quantidadeAgendamentos) {
        var usuariosAgendamentos = recuperarUsuariosComAgendamentosPredistribuidos(
                usuarioId,
                aaPermitido.getId(),
                quantidadeAgendamentos);

        long agendamentosRestantes = quantidadeAgendamentos % usuariosAgendamentos.size();

        return new AgendamentoDistribuicaoListagemResponse(
                aaPermitido.getId(),
                aaPermitido.getCnpjRazaoSocial(),
                equipeVendasSupervisor.getEquipeVendasNome(),
                equipeVendasSupervisor.getSupervisorNome(),
                quantidadeAgendamentos,
                agendamentosRestantes,
                usuariosAgendamentos);
    }

    private List<AgendamentoUsuarioDto> recuperarUsuariosComAgendamentosPredistribuidos(Integer usuarioId,
                                                                                        Integer agenteAutorizadoId,
                                                                                        Long quantidadeTotal) {
        var usuariosAa = recuperarUsuariosParaDistribuicao(usuarioId, agenteAutorizadoId);
        var totalUsuarios = usuariosAa.size();
        var agendamentosPorUsuario = quantidadeTotal / totalUsuarios;

        return usuariosAa.stream()
                .map(usuario -> new AgendamentoUsuarioDto(usuario, agendamentosPorUsuario))
                .sorted(Comparator.comparing(AgendamentoUsuarioDto::getNome))
                .collect(Collectors.toList());
    }

    private List<UsuarioAgenteAutorizadoAgendamentoResponse> recuperarUsuariosParaDistribuicao(Integer usuarioId,
                                                                                               Integer agenteAutorizadoId) {
        var cargoDoUsuario = cargoService.findByUsuarioId(usuarioId);

        var usuariosDoAa = agenteAutorizadoService.getUsuariosByAaId(agenteAutorizadoId, false)
                .stream()
                .map(UsuarioAgenteAutorizadoResponse::getId)
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

    public void distribuirAgendamentosDoUsuario(AgendamentoDistribuicaoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        var totalAgendamentos = request.getAgendamentosPorUsuario()
                .stream()
                .map(AgendamentoUsuarioDto::getQuantidade)
                .reduce(0L, Long::sum);

        if (!isQuantidadeAgendamentosValida(request.getAgenteAutorizadoId(), request.getUsuarioOrigemId(), totalAgendamentos)) {
            throw new ValidacaoException("Quantidade de agendamentos enviada é inválida.");
        }

        distribuirTabulacoesMqSender.distribuirTabulacoes(new TabulacaoDistribuicaoMqRequest(
                request.getAgenteAutorizadoId(),
                request.getUsuarioOrigemId(),
                request.getAgendamentosPorUsuario(),
                usuarioAutenticado.getId(),
                usuarioAutenticado.getNome()));
    }

    private boolean isQuantidadeAgendamentosValida(Integer aaId, Integer usuarioOrigemId, long totalAgendamentosEnviados) {
        var totalAgendamentos = new AtomicLong(0L);

        tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(usuarioOrigemId)
                .stream()
                .filter(a -> a.getAgenteAutorizadoId().equals(aaId))
                .findFirst()
                .map(AgendamentoAgenteAutorizadoResponse::getQuantidadeAgendamentos)
                .ifPresent(totalAgendamentos::set);

        return totalAgendamentosEnviados == totalAgendamentos.get();
    }
}
