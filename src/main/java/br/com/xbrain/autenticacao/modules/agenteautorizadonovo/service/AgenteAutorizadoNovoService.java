package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
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
public class AgenteAutorizadoNovoService {

    @Autowired
    private AgenteAutorizadoNovoClient client;

    public List<UsuarioDtoVendas> buscarTodosUsuariosDosAas(List<Integer> aasIds, Boolean buscarInativos) {
        try {
            return client.buscarTodosUsuariosDosAas(aasIds, buscarInativos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoNovoService.class.getName(),
                EErrors.ERRO_BUSCAR_TODOS_USUARIOS_DOS_AAS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}