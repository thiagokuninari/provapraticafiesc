package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ColaboradorVendasService {

    @Autowired
    private ColaboradorVendasClient colaboradorVendasClient;

    public void inativarColaborador(String email) {
        try {
            colaboradorVendasClient.inativarColaboradorPeloEmail(email);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    ColaboradorVendasService.class.getName(),
                    EErrors.ERRO_INATIVAR_COLABORADOR_VENDAS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

}
