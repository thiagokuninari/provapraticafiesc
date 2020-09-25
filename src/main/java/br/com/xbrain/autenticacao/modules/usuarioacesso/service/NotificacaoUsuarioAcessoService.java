package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificacaoUsuarioAcessoService {

    @Autowired
    private NotificacaoUsuarioAcessoClient client;
    @Autowired
    private ObjectMapper objectMapper;

    public MongoosePage<LoginLogoutResponse> getLoginsLogoutsDeHoje(
        Optional<? extends Collection<Integer>> usuariosIds,
        PageRequest pageRequest) {
        try {
            var type = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
            Map<String, Object> pageRequestParams = objectMapper.convertValue(pageRequest, type);
            usuariosIds.ifPresent(ids -> {
                var usuariosIdsParam = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
                pageRequestParams.put("usuariosIds", usuariosIdsParam);
            });

            return client.getLoginsLogoutsDeHoje(pageRequestParams);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_HOJE);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<LoginLogoutCsv> getCsv(
        RelatorioLoginLogoutCsvFiltro filtro,
        Optional<? extends Collection<Integer>> usuariosIdsPermitidos) {
        try {
            return client.getCsv(filtro.toFeignRequestMap(usuariosIdsPermitidos));
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_CSV);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Integer> getUsuariosIdsByIds(Optional<List<Integer>> usuariosIds) {
        try {
            if (usuariosIds.isPresent() && usuariosIds.get().isEmpty()) {
                return List.of();
            }
            return client.getUsuariosIdsByIds(usuariosIds.orElse(null)).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_USUARIOS_IDS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
