package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.DirecionamentoInsideSalesVendedorClient;
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
public class DirecionamentoInsideSalesVendedorService {

    private final DirecionamentoInsideSalesVendedorClient client;

    public void inativarDirecionamentoPorUsuarioVendedorId(Integer usuarioVendedorId) {
        try {
            client.inativarDirecionamentoPorUsuarioVendedorId(usuarioVendedorId);
        } catch (RetryableException ex) {
            log.error("Erro ao inativar direcionamentos vinculados ao vendedor: {}", usuarioVendedorId);
            throw new IntegracaoException(ex, DirecionamentoInsideSalesVendedorService.class.getName(),
                EErrors.ERRO_INATIVAR_DIRECIONAMENTOS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
