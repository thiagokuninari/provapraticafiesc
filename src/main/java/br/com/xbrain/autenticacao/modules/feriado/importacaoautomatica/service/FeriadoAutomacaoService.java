package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriadoAutomacaoService {

    private final FeriadoAutomacaoClient feriadoAutomacaoClient;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 2))
    public List<FeriadoAutomacao> consultarFeriadosNacionais(Integer ano) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosNacionais(ano);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 2))
    public List<FeriadoAutomacao> consultarFeriadosEstaduais(Integer ano, String uf) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosEstaduais(ano, uf);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 2))
    public List<FeriadoAutomacao> consultarFeriadosMunicipais(Integer ano, String uf, String cidade) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosMunicipais(ano, uf, cidade);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

}
