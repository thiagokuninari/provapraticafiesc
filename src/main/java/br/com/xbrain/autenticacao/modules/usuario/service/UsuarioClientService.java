package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.client.UsuarioClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioClientService {

    @Autowired
    private UsuarioClient client;

    public void alterarSituacao(Integer id) {
        try {
            client.alterarSituacao(id);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                UsuarioService.class.getName(),
                EErrors.ERRO_ALTERAR_SITUACAO_USUARIO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
