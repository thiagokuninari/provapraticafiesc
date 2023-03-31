package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    private static final int COLUNA_CARGO_CODIGO = 4;

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private EquipeVendaD2dService equipeVendaD2dService;
    @Autowired
    private RegionalService regionalService;

    public List<UsuarioResponse> getCargosDescendentesEVendedoresD2dDoSupervisor(Integer supervisorId,
                                                                                 Integer equipeId,
                                                                                 Integer subCanalId) {
        var vendedoresDoSupervisor = filtrarUsuariosParaAderirAEquipe(equipeId,
            Objects.nonNull(subCanalId)
                ? getVendedoresDoSupervisor(supervisorId, subCanalId)
                : getVendedoresDoSupervisor(supervisorId));
        var cargosDescendentesDoSupervisor = Objects.nonNull(subCanalId)
            ? getCargosDescendentesDoSupervisor(supervisorId,
                getCanalBySupervisorId(supervisorId), subCanalId)
            : getCargosDescendentesDoSupervisor(supervisorId,
                getCanalBySupervisorId(supervisorId));

        return Stream.concat(cargosDescendentesDoSupervisor.stream(),
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

    private List<UsuarioResponse> getCargosDescendentesDoSupervisor(Integer supervisorId,
                                                                    ECanal canal) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
            supervisorId,
            List.of(ASSISTENTE_OPERACAO, OPERACAO_ANALISTA, OPERACAO_CONSULTOR),
            canal);
    }

    private List<UsuarioResponse> getCargosDescendentesDoSupervisor(Integer supervisorId,
                                                                    ECanal canal,
                                                                    Integer subCanalId) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
            supervisorId,
            List.of(ASSISTENTE_OPERACAO, OPERACAO_ANALISTA, OPERACAO_CONSULTOR),
            canal,
            subCanalId);
    }

    private List<UsuarioResponse> getVendedoresDoSupervisor(Integer supervisorId) {
        return usuarioRepository
                .getSubordinadosPorCargo(supervisorId,
                    Set.of(CodigoCargo.VENDEDOR_OPERACAO.name(), CodigoCargo.OPERACAO_EXECUTIVO_VENDAS.name()))
                .stream()
                .map(row -> new UsuarioResponse(
                    ((BigDecimal) row[COLUNA_USUARIO_ID]).intValue(),
                    (String) row[COLUNA_USUARIO_NOME],
                    valueOf((String) row[COLUNA_CARGO_CODIGO])))
                .collect(Collectors.toList());
    }

    private List<UsuarioResponse> getVendedoresDoSupervisor(Integer supervisorId, Integer subCanalId) {
        return usuarioRepository
                .getSubordinadosPorCargo(supervisorId,
                    Set.of(CodigoCargo.VENDEDOR_OPERACAO.name(), CodigoCargo.OPERACAO_EXECUTIVO_VENDAS.name()),
                    subCanalId)
                .stream()
                .map(row -> new UsuarioResponse(
                    ((BigDecimal) row[COLUNA_USUARIO_ID]).intValue(),
                    (String) row[COLUNA_USUARIO_NOME],
                    valueOf((String) row[COLUNA_CARGO_CODIGO])))
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getSupervisoresPorAreaAtuacao(AreaAtuacao areaAtuacao, List<Integer> areasAtuacaoId) {
        var porRegional = AreaAtuacao.REGIONAL.equals(areaAtuacao) 
            && regionalService.getNovasRegionaisIds().contains(areasAtuacaoId.get(0));
        var porUf = AreaAtuacao.UF.equals(areaAtuacao);
        return porRegional || porUf
            ? usuarioRepository.getUsuariosPorNovaAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                List.of(SUPERVISOR_OPERACAO),
                Set.of(D2D_PROPRIO))
            : usuarioRepository.getUsuariosPorAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                List.of(SUPERVISOR_OPERACAO),
                Set.of(D2D_PROPRIO));
    }

    public List<UsuarioResponse> getLideresPorAreaAtuacao(AreaAtuacao areaAtuacao, List<Integer> areasAtuacaoId) {
        var porRegional = AreaAtuacao.REGIONAL.equals(areaAtuacao) 
            && regionalService.getNovasRegionaisIds().contains(areasAtuacaoId.get(0));
        var porUf = AreaAtuacao.UF.equals(areaAtuacao);
        return porRegional || porUf
            ? usuarioRepository.getUsuariosPorNovaAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                List.of(SUPERVISOR_OPERACAO,COORDENADOR_OPERACAO),
                Set.of(D2D_PROPRIO))
            : usuarioRepository.getUsuariosPorAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                List.of(SUPERVISOR_OPERACAO,COORDENADOR_OPERACAO),
                Set.of(D2D_PROPRIO));
    }

    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuarioPeloCanal(Integer usuarioId, ECanal canal) {
        return usuarioRepository.getSupervisoresDoSubclusterDoUsuarioPeloCanal(usuarioId, canal);
    }
}
