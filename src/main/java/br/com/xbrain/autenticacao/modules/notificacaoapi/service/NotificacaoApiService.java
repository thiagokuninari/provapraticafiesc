package br.com.xbrain.autenticacao.modules.notificacaoapi.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacaoApiService {

    private final NotificacaoClient notificacaoClient;

    public boolean consultarStatusTabulacaoByUsuario(Integer usuarioId) {
        try {
            return notificacaoClient.consultarStatusTabulacaoByUsuario(usuarioId);
        } catch (HystrixBadRequestException | RetryableException ex) {
            log.warn("Erro ao tentar consultar status de tabulação pelo usuário");
            throw new IntegracaoException(ex, NotificacaoApiService.class.getName(),
                    EErrors.ERRO_CONSULTAR_STATUS_TABULACAO_USUARIO);
        }
    }
}
