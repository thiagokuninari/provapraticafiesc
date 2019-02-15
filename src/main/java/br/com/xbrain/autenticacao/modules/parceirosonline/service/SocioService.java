package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocioService {

    @Autowired
    private SocioClient socioClient;

    public SocioResponse findSocioPrincipalByAaId(Integer agenteAutorizadoId) {
        try {
            return socioClient.findSocioPrincipalByAaId(agenteAutorizadoId);
        } catch (RetryableException ex) {
            log.warn("Erro ao tentar recuperar o sócio principal do agente autorizado");
            throw new IntegracaoException(ex, SocioService.class.getName(), EErrors.ERRO_OBTER_SOCIO_PRINCIPAL_BY_AA_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
