package br.com.xbrain.autenticacao.modules.suportevendas.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.suportevendas.client.SuporteVendasClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuporteVendasService {

    private final SuporteVendasClient client;

    public boolean existsGrupoByUsuarioAndOrganizacaoNot(Integer id, Integer novaOrganizacaoId) {
        try {
            return client.existsGrupoByUsuarioAndOrganizacaoNot(id, novaOrganizacaoId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                SuporteVendasService.class.getName(),
                EErrors.ERRO_VERIFICAR_GRUPO_SUPORTE_VENDAS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
