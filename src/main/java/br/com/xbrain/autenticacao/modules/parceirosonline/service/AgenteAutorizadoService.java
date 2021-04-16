package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenteAutorizadoService {

    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoComVendedoresD2D(Integer aaId) {
        try {
            return agenteAutorizadoClient.getUsuariosAaAtivoComVendedoresD2D(aaId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosByAaIdCanalDoUsuario(Integer aaId, Integer usuarioId) {
        try {
            return agenteAutorizadoClient.getUsuariosByAaIdCanalDoUsuario(aaId, usuarioId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
