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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificacaoUsuarioAcessoService {

    @Autowired
    private NotificacaoUsuarioAcessoClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public MongoosePage<LoginLogoutResponse> getLoginsLogoutsDeHoje(Collection<Integer> usuariosIds, PageRequest pageRequest) {
        try {
            var pageRequestParams = objectMapper.convertValue(pageRequest, Map.class);
            pageRequestParams.put("usuariosIds", usuariosIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

            return client.getLoginsLogoutsDeHoje(pageRequestParams);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_HOJE);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<LoginLogoutCsv> getCsv(RelatorioLoginLogoutCsvFiltro filtro, Collection<Integer> usuariosIdsPermitidos) {
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
}
