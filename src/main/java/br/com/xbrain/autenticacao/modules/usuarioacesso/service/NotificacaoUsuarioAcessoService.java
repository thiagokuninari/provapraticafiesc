package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificacaoUsuarioAcessoService {

    @Autowired
    private NotificacaoUsuarioAcessoClient client;

    public List<PaLogadoDto> countUsuariosLogadosPorPeriodo(UsuarioLogadoRequest usuarioLogadoRequest) {
        try {
            return client.countUsuariosLogadosPorPeriodo(usuarioLogadoRequest);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_LOGADOS_POR_HORA);
        }
    }
}
