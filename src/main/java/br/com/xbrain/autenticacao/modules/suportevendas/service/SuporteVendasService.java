package br.com.xbrain.autenticacao.modules.suportevendas.service;

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

    public void desvincularGruposByUsuarioId(Integer id) {
        try {
            client.desvincularGruposByUsuarioId(id);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                SuporteVendasService.class.getName(),
                "Ocorreu um erro ao desvincular grupo do usuário no suporte-vendas.");
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
