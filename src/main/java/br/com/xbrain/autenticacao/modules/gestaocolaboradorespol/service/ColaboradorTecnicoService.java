package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorTecnicoClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRemanejamentoRequest;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColaboradorTecnicoService {

    private final ColaboradorTecnicoClient client;

    public void atualizarUsuarioRemanejado(UsuarioRemanejamentoRequest request) {
        try {
            log.info("Atualizando colaborador remanejado no Parceiros Online.");
            client.atualizarUsuarioRemanejado(request);
            log.info("Colaborador atualizado com sucesso.");
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ColaboradorTecnicoService.class.getName(),
                EErrors.ERRO_REMANEJAR_COLABORADOR_POL
            );
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

}
