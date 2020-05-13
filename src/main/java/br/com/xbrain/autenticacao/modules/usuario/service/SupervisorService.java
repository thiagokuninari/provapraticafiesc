package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupervisorService {

    private static final int COLUNA_USUARIO_ID = 0;
    private static final int COLUNA_USUARIO_NOME = 1;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipeVendaD2dService equipeVendaD2dService;

    @Autowired
    private AutenticacaoService autenticacaoService;

    private static final Set<ECanal> CANAIS_PADRAO = Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO);

    public List<UsuarioResponse> getAssistentesEVendedoresD2dDoSupervisor(Integer supervisorId, Integer equipeId) {
        var vendedoresDoSupervisor = filtrarUsuariosParaAderirAEquipe(equipeId, getVendedoresDoSupervisor(supervisorId));
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        var canais = CANAIS_PADRAO;
        if (!isEmpty(usuarioAutenticado) && !usuarioAutenticado.getCanais().isEmpty()) {
            canais = usuarioAutenticado.getCanais();
        }
        return Stream.concat(
            getAssistentesDoSupervisor(supervisorId, canais).stream(),
            vendedoresDoSupervisor.stream())
            .sorted(Comparator.comparing(UsuarioResponse::getNome))
            .collect(Collectors.toList());
    }

    private List<UsuarioResponse> filtrarUsuariosParaAderirAEquipe(Integer equipeId,
                                                                   List<UsuarioResponse> vendedoresDoSupervisor) {
        return equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(vendedoresDoSupervisor, equipeId);
    }

    private List<UsuarioResponse> getAssistentesDoSupervisor(Integer supervisorId, Set<ECanal> canais) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
            supervisorId,
            List.of(ASSISTENTE_OPERACAO),
            canais);
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

    public List<UsuarioResponse> getSupervisoresPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                               List<Integer> areasAtuacaoId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        var canais = CANAIS_PADRAO;
        if (!isEmpty(usuarioAutenticado) && !usuarioAutenticado.getCanais().isEmpty()) {
            canais = usuarioAutenticado.getCanais();
        }
        return usuarioRepository.getUsuariosPorAreaAtuacao(
            areaAtuacao,
            areasAtuacaoId,
            SUPERVISOR_OPERACAO,
            canais);
    }

    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuario(Integer usuarioId) {
        return usuarioRepository.getSupervisoresSubclusterDoUsuario(usuarioId);
    }
}
