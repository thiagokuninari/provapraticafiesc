package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.ClaroIndicoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaroIndicoService {

    private final ClaroIndicoClient client;

    public void desvincularUsuarioDaFilaTratamento(Integer id) {
        try {
            client.desvincularUsuarioDaFilaTratamento(id);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ClaroIndicoService.class.getName(),
                "Ocorreu um erro ao desvincular usuário da fila de tratamento.");
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
