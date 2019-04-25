package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.mailing.service.TabulacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.DistribuirTabulacoesMqSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioAgendamentoService {
    private final AutenticacaoService autenticacaoService;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final TabulacaoService tabulacaoService;
    private final ColaboradorVendasService colaboradorVendasService;
    private final DistribuirTabulacoesMqSender distribuirTabulacoesMqSender;

    public List<AgendamentoDistribuicaoListagemResponse> getAgendamentoDistribuicaoDoUsuario(Integer usuarioId) {
        Map<Integer, Long> agendamentos =
                agruparAgendamentosPorAgenteAutorizado(
                        tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(usuarioId));

        EquipeVendasSupervisorResponse colaboradorVendas =
                colaboradorVendasService.getEquipeVendasSupervisorDoUsuarioId(usuarioId)
                .orElseGet(() -> new EquipeVendasSupervisorResponse(null, null));

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
        List<AgendamentoUsuarioDto> usuariosAgendamentos =
                recuperarUsuariosComAgendamentosPredistribuidos(
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
        List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosAa =
                agenteAutorizadoService.getUsuariosByAaIdCanalDoUsuario(agenteAutorizadoId, usuarioId);
        long totalUsuarios = usuariosAa.size();
        long agendamentosPorUsuario = quantidadeTotal / totalUsuarios;

        return usuariosAa.stream()
                .map(usuario -> new AgendamentoUsuarioDto(usuario, agendamentosPorUsuario))
                .sorted(Comparator.comparing(AgendamentoUsuarioDto::getNome))
                .collect(Collectors.toList());
    }

    public void distribuirAgendamentosDoUsuario(AgendamentoDistribuicaoRequest request) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        long totalAgendamentos = request.getAgendamentosPorUsuario()
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
        AtomicLong totalAgendamentos = new AtomicLong(0L);

        tabulacaoService.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(usuarioOrigemId)
                .stream()
                .filter(a -> a.getAgenteAutorizadoId().equals(aaId))
                .findFirst()
                .map(AgendamentoAgenteAutorizadoResponse::getQuantidadeAgendamentos)
                .ifPresent(totalAgendamentos::set);

        return totalAgendamentosEnviados == totalAgendamentos.get();
    }
}
