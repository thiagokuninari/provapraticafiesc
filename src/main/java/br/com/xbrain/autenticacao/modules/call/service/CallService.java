package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.modules.call.dto.ConfiguracaoTelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.SuporteVendasBkoRequest;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_ATUALIZAR_CONFIGURACAO_FORNECEDOR;
import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_SALVAR_CONFIGURACAO_FORNECEDOR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {

    private final CallClient callClient;

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

    public List<RamalResponse> obterRamaisParaCanal(ECanal canal, Integer agenteAutorizadoId) {
        try {
            return callClient.obterRamaisParaCanal(canal, agenteAutorizadoId);
        } catch (RetryableException ex) {
            log.warn("Erro ao tentar recuperar a lista de ramais");
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_OBTER_LISTA_RAMAIS_BY_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void cleanCacheFeriadosTelefonia() {
        try {
            callClient.cleanCacheFeriadosTelefonia();
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex, CallService.class.getName(), EErrors.ERRO_LIMPAR_CACHE_CALL);
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

    public List<ConfiguracaoTelefoniaResponse> getDiscadoras() {
        return callClient.getDiscadoras();
    }

    public void desvincularDiscadoraERamaisSuporteVendas(Integer organizacaoId) {
        try {
            callClient.desvicularDiscadoraSuporteVendas(organizacaoId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException("Erro ao tentar desvincular discadora e ramais.");
        }
    }

    public void ativarConfiguracaoSuporteVendas(Integer organizacaoId) {
        try {
            callClient.ativarConfiguracaoSuporteVendas(organizacaoId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException("Erro ao tentar ativar configuração");
        }
    }

    public void salvarConfiguracaoSuporteVendas(Integer fornecedorId, String nome) {
        try {
            callClient.salvarConfiguracaoSuporteVendas(SuporteVendasBkoRequest.of(fornecedorId, nome));
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex, CallService.class.getName(), ERRO_SALVAR_CONFIGURACAO_FORNECEDOR);
        }
    }

    public void atualizarConfiguracaoSuporteVendas(Integer fornecedorId, String nome) {
        try {
            callClient.atualizarConfiguracaoSuporteVendas(fornecedorId, SuporteVendasBkoRequest.of(nome));
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex, CallService.class.getName(), ERRO_ATUALIZAR_CONFIGURACAO_FORNECEDOR);
        }
    }
}
