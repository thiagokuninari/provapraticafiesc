package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.IndicacaoInsideSalesPmeClient;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicacaoInsideSalesPmeService {
    private final IndicacaoInsideSalesPmeClient client;

    public void redistribuirIndicacoesPorUsuarioVendedorId(Integer usuarioVendedorId) {
        try {
            client.redistribuirIndicacoesPorUsuarioVendedorId(usuarioVendedorId);
        } catch (RetryableException ex) {
            log.error("Erro ao redistribuir HPs Inside Sales do usuário: {}", usuarioVendedorId);
            throw new IntegracaoException(ex, DirecionamentoInsideSalesVendedorService.class.getName(),
                EErrors.ERRO_REDISTRIBUIR_INSIDE_SALES);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
