package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.*;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.google.common.collect.Lists;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class NotificacaoUsuarioAcessoService {

    private static final int USUARIOS_IDS_PART_SIZE = 100;

    @Autowired
    private NotificacaoUsuarioAcessoClient client;

    public MongoosePage<LoginLogoutResponse> getLoginsLogoutsDeHoje(
        Optional<? extends Collection<Integer>> usuariosIds,
        PageRequest pageRequest) {
        try {
            if (usuariosIds.isPresent() && usuariosIds.get().isEmpty()) {
                return MongoosePage.empty();
            }

            return client.getLoginsLogoutsDeHoje(GetLoginLogoutHojeRequest.of(usuariosIds, pageRequest));
        } catch (RetryableException | HystrixBadRequestException ex) {
            log.error("Erro ao consultar os Logins / Logouts de hoje.", ex);
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_HOJE);
        }
    }

    public List<LoginLogoutResponse> buscarAcessosEntreDatasPorUsuarios(RelatorioLoginLogoutRequest request) {
        try {
            return client.getLoginsLogoutsEntreDatas(request);
        } catch (RetryableException | HystrixBadRequestException ex) {
            log.error("Erro ao consultar os Logins / Logouts entre datas.", ex);
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_ENTRE_DATAS);
        }
    }

    public List<LoginLogoutCsv> getCsv(
        RelatorioLoginLogoutCsvFiltro filtro,
        Optional<? extends Collection<Integer>> usuariosIdsPermitidos) {
        try {
            if (usuariosIdsPermitidos.isPresent() && usuariosIdsPermitidos.get().isEmpty()) {
                return List.of();
            }
            return usuariosIdsPermitidos
                .map(ids -> Lists.partition(List.copyOf(ids), USUARIOS_IDS_PART_SIZE))
                .map(idsParts -> idsParts.parallelStream()
                    .map(idsPart -> getCsvPart(filtro, Optional.of(idsPart)))
                    .flatMap(Collection::stream))
                .orElseGet(() -> getCsvPart(filtro, Optional.empty()).stream())
                .collect(Collectors.toList());
        } catch (RetryableException | HystrixBadRequestException ex) {
            log.error("Erro ao buscar relatório de Login / Logout.", ex);
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_CSV);
        }
    }

    private List<LoginLogoutCsv> getCsvPart(
        RelatorioLoginLogoutCsvFiltro filtro,
        Optional<? extends Collection<Integer>> usuariosIdsPermitidos) {
        if (usuariosIdsPermitidos.isPresent() && usuariosIdsPermitidos.get().isEmpty()) {
            return List.of();
        }
        var request = filtro.toFeignRequestMap(usuariosIdsPermitidos);
        return !ObjectUtils.isEmpty(request.get("usuariosIds")) ? client.getCsv(request) : List.of();
    }

    public List<Integer> getUsuariosIdsByIds(Optional<? extends Collection<Integer>> usuariosIds) {
        try {
            if (usuariosIds.isPresent() && usuariosIds.get().isEmpty()) {
                return List.of();
            }
            return usuariosIds
                .map(ids -> Lists.partition(List.copyOf(ids), USUARIOS_IDS_PART_SIZE))
                .map(idsParts -> idsParts.parallelStream()
                    .map(idsPart -> client.getUsuariosIdsByIds(idsPart))
                    .flatMap(Collection::stream))
                .orElseGet(() -> client.getUsuariosIdsByIds(null).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (RetryableException | HystrixBadRequestException ex) {
            log.error("Erro ao consultar os usuários do relatório de Login / Logout.", ex);
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_USUARIOS_IDS);
        }
    }

    public List<PaLogadoDto> countUsuariosLogadosPorPeriodo(UsuarioLogadoRequest usuarioLogadoRequest) {
        try {
            return client.countUsuariosLogadosPorPeriodo(usuarioLogadoRequest);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_LOGADOS_POR_HORA);
        }
    }

    public List<Integer> getUsuariosLogadosAtualPorIds(List<Integer> usuarioIds) {
        try {
            return Optional.ofNullable(usuarioIds)
                .filter(ids -> !isEmpty(ids))
                .map(ids -> client.getUsuariosLogadosAtualPorIds(ids))
                .orElse(List.of());
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_LOGADOS_POR_IDS);
        }
    }

    public List<UsuarioLogadoResponse> getUsuariosLogadosComDataEntradaPorIds(List<Integer> usuariosIds) {
        try {
            return client.getUsuariosLogadosAtualComDataEntradaPorIds(usuariosIds);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                "Erro ao tentar buscar usuarios logados.");
        }
    }
}
