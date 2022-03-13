package br.com.xbrain.autenticacao.modules.mailing.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.mailing.client.MailingClient;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailingService {

    @Autowired
    private MailingClient client;

    public Long countQuantidadeAgendamentosProprietariosDoUsuario(Integer usuarioId, ECanal canal) {
        try {
            return client.countQuantidadeAgendamentosProprietariosDoUsuario(usuarioId, canal);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                UsuarioService.class.getName(),
                EErrors.ERRO_OBTER_QUANTIDADE_AGENDAMENTOS_USUARIO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void flushCacheFeriados() {
        try {
            client.cleanCacheFeriados();
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex, MailingService.class.getName(), EErrors.ERRO_LIMPAR_CACHE_FERIADOS_MAILING);
        }
    }
}
