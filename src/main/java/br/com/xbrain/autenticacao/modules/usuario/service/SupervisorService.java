package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SupervisorService {

    private static final int COLUNA_USUARIO_ID = 0;
    private static final int COLUNA_USUARIO_NOME = 1;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipeVendaService equipeVendaService;

    public List<UsuarioResponse> getAssistentesEVendedoresD2dDoSupervisor(Integer supervisorId) {

        return Stream.concat(
            getAssistentesDoSupervisor(supervisorId).stream(),
            removerVendedoresComEquipe(getVendedoresDoSupervisor(supervisorId)).stream())
            .sorted(Comparator.comparing(UsuarioResponse::getNome))
            .collect(Collectors.toList());
    }

    private List<UsuarioResponse> removerVendedoresComEquipe(List<UsuarioResponse> vendedoresDoSupervisor) {
        return equipeVendaService.filtrarUsuariosSemEquipe(vendedoresDoSupervisor);
    }

    private List<UsuarioResponse> getAssistentesDoSupervisor(Integer supervisorId) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
            supervisorId,
            List.of(CodigoCargo.ASSISTENTE_OPERACAO),
            ECanal.D2D_PROPRIO);
    }

    private List<UsuarioResponse> getVendedoresDoSupervisor(Integer supervisorId) {
        return usuarioRepository
                .getSubordinadosPorCargo(supervisorId, CodigoCargo.VENDEDOR_OPERACAO.name())
                .stream()
                .map(row -> new UsuarioResponse(
                        ((BigDecimal) row[COLUNA_USUARIO_ID]).intValue(),
                        (String) row[COLUNA_USUARIO_NOME],
                        CodigoCargo.VENDEDOR_OPERACAO))
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getSupervisoresPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                               List<Integer> areasAtuacaoId) {
        return usuarioRepository.getUsuariosPorAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                CodigoCargo.SUPERVISOR_OPERACAO,
                ECanal.D2D_PROPRIO);
    }
}
