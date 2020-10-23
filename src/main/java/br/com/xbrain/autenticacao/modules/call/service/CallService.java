package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CallService {

    @Autowired
    private CallClient callClient;

    public TelefoniaResponse obterNomeTelefoniaPorId(Integer discadoraId) {
        try {
            return callClient.obterNomeTelefoniaPorId(discadoraId);
        } catch (RetryableException ex) {
            log.warn("Erro ao tentar recuperar a discadora pelo id");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_OBTER_DISCADORA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<RamalResponse> obterRamaisParaAgenteAutorizado(Integer agenteAutorizadoId) {
        try {
            return callClient.obterRamaisParaAgenteAutorizado(agenteAutorizadoId);
        } catch (RetryableException ex) {
            log.warn("Erro ao tentar recuperar a lista de ramais do agente autorizado");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_OBTER_LISTA_RAMAIS_BY_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void desvincularRamaisDaDiscadoraAtivoProprio(Integer siteId, Integer discadoraId) {
        try {
            callClient.desvincularRamaisDaDiscadoraAtivoProprio(siteId, discadoraId);
        } catch (RetryableException ex) {
            log.warn("Erro ao desvincular ramais");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_DESVINCULAR_RAMAIS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void cleanCacheableSiteAtivoProprio() {
        try {
            callClient.cleanCacheableSiteAtivoProprio();
        } catch (RetryableException ex) {
            log.warn("Erro ao limpar cache ativo");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_LIMPAR_CACHE_ATIVO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public boolean consultarStatusUsoRamalByUsuarioAutenticado() {
        try {
            return callClient.consultarStatusUsoRamalByUsuarioAutenticado();
        } catch (HystrixBadRequestException | RetryableException ex) {
            log.warn("Erro ao tentar consultar status do ramal pelo usuário autenticado");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_CONSULTAR_STATUS_RAMAL_USUARIO);
        }
    }

    public void liberarRamalUsuarioAutenticado() {
        try {
            callClient.liberarRamalUsuarioAutenticado();
        } catch (HystrixBadRequestException | RetryableException ex) {
            log.warn("Erro ao tentar liberar o ramal do usuário autenticado");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_LIBERAR_RAMAL_USUARIO);
        }
    }
}
