package br.com.xbrain.autenticacao.modules.agenteautorizado.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.client.SocioClient;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.SocioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioClient client;

    public SocioResponse findSocioPrincipalByAaId(Integer agenteAutorizadoId) {
        try {
            return client.findSocioPrincipalByAaId(agenteAutorizadoId);
        } catch (RetryableException ex) {
            log.warn("Erro ao tentar recuperar o sócio principal do agente autorizado");
            throw new IntegracaoException(ex, SocioService.class.getName(), EErrors.ERRO_OBTER_SOCIO_PRINCIPAL_BY_AA_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
