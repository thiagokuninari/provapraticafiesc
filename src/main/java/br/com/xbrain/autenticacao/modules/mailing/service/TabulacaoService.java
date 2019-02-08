package br.com.xbrain.autenticacao.modules.mailing.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.mailing.client.TabulacaoClient;
import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TabulacaoService {

    private final TabulacaoClient tabulacaoClient;

    public List<AgendamentoAgenteAutorizadoResponse> getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(Integer usuarioId) {
        try {
            return tabulacaoClient.getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(usuarioId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_DISTRIBUIR_TABULACOES);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<TabulacaoDistribuicaoResponse> distribuirAgendamentosProprietariosDoUsuario(
            TabulacaoDistribuicaoRequest request) {
        try {
            return tabulacaoClient.distribuirAgendamentosProprietariosDoUsuario(request);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_DISTRIBUIR_TABULACOES);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
