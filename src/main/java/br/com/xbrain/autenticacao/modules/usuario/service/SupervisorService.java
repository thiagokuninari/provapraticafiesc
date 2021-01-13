package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.ATIVO_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;

@Service
@RequiredArgsConstructor
public class SupervisorService {

    private static final int COLUNA_USUARIO_ID = 0;
    private static final int COLUNA_USUARIO_NOME = 1;

    private final UsuarioRepository usuarioRepository;
    private final EquipeVendaD2dService equipeVendaD2dService;

    public List<UsuarioResponse> getAssistentesEVendedoresDoSupervisor(Integer supervisorId, Integer equipeId) {
        var vendedoresDoSupervisor = filtrarUsuariosParaAderirAEquipe(equipeId, getVendedoresDoSupervisor(supervisorId));

        return Stream.concat(
            getAssistentesDoSupervisor(supervisorId, getCanalBySupervisorId(supervisorId)).stream(),
            vendedoresDoSupervisor.stream())
            .sorted(Comparator.comparing(UsuarioResponse::getNome))
            .collect(Collectors.toList());
    }

    private ECanal getCanalBySupervisorId(Integer supervisorId) {
        var supervisor = usuarioRepository.findById(supervisorId)
            .orElseThrow(() -> new ValidacaoException("Supervisor não encontrado."));

        if (supervisor.hasCanal(ATIVO_PROPRIO)) {
            return ATIVO_PROPRIO;
        } else if (supervisor.hasCanal(D2D_PROPRIO)) {
            return D2D_PROPRIO;
        }
        throw new ValidacaoException("O supervisor deve ser do canal Ativo Próprio ou D2D Próprio.");
    }

    private List<UsuarioResponse> filtrarUsuariosParaAderirAEquipe(Integer equipeId,
                                                                   List<UsuarioResponse> vendedoresDoSupervisor) {
        return equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(vendedoresDoSupervisor, equipeId);
    }

    private List<UsuarioResponse> getAssistentesDoSupervisor(Integer supervisorId, ECanal canal) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
            supervisorId,
            List.of(ASSISTENTE_OPERACAO),
            canal);
    }

    private List<UsuarioResponse> getVendedoresDoSupervisor(Integer supervisorId) {
        return usuarioRepository
            .getSubordinadosPorCargo(supervisorId, Set.of(VENDEDOR_OPERACAO.name(), OPERACAO_TELEVENDAS.name()))
            .stream()
            .map(row -> new UsuarioResponse(
                ((BigDecimal) row[COLUNA_USUARIO_ID]).intValue(),
                (String) row[COLUNA_USUARIO_NOME],
                VENDEDOR_OPERACAO))
            .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getSupervisoresPorAreaAtuacao(AreaAtuacao areaAtuacao, List<Integer> areasAtuacaoId) {
        return usuarioRepository.getUsuariosPorAreaAtuacao(
            areaAtuacao,
            areasAtuacaoId,
            SUPERVISOR_OPERACAO,
            Set.of(D2D_PROPRIO));
    }

    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuarioPeloCanal(Integer usuarioId, ECanal canal) {
        return usuarioRepository.getSupervisoresDoSubclusterDoUsuarioPeloCanal(usuarioId, canal);
    }
}
