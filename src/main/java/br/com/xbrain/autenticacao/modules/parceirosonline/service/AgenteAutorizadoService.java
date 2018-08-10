package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgenteAutorizadoService {

    private final Logger logger = LoggerFactory.getLogger(AgenteAutorizadoService.class);

    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;

    public List<Integer> getIdUsuariosPorAa(String cnpj) {
        try {
            AgenteAutorizadoResponse aaResponse = getAaByCpnj(cnpj);
            return getUsuariosByAaId(Integer.valueOf(aaResponse.getId())).stream()
                    .map(UsuarioAgenteAutorizadoResponse::getId)
                    .collect(Collectors.toList());
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_CNPJ);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    private AgenteAutorizadoResponse getAaByCpnj(String cnpj) {
        try {
            AgenteAutorizadoRequest request = new AgenteAutorizadoRequest();
            request.setCnpj(cnpj);
            Map map = new ObjectMapper().convertValue(request, Map.class);
            return agenteAutorizadoClient.getAaByCpnj(map);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_CNPJ);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    private List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(Integer aaId) {
        try {
            return agenteAutorizadoClient.getUsuariosByAaId(aaId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    @Cacheable("getAasPermitidos")
    public List<Integer> getAasPermitidos(int usuarioId) {
        try {
            return agenteAutorizadoClient.getAasPermitidos(usuarioId);
        } catch (Exception ex) {
            logger.warn("Erro ao consultar agentes autorizados do usuário", ex);
            return Collections.emptyList();
        }
    }
}
