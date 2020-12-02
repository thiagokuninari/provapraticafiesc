package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.GetLoginLogoutHojeRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.google.common.collect.Lists;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
                    .map(idsPart -> client.getCsv(filtro.toFeignRequestMap(Optional.of(idsPart))))
                    .flatMap(Collection::stream))
                .orElseGet(() -> client.getCsv(filtro.toFeignRequestMap(Optional.empty())).stream())
                .collect(Collectors.toList());
        } catch (RetryableException | HystrixBadRequestException ex) {
            log.error("Erro ao buscar relatório de Login / Logout.", ex);
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_CSV);
        }
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
}
