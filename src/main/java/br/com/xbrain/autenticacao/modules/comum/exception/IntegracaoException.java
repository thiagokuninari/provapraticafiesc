package br.com.xbrain.autenticacao.modules.comum.exception;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.model.MessageException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.core.NestedExceptionUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntegracaoException extends RuntimeException {

    public IntegracaoException(String mensagem) {
        super(mensagem);
    }

    public IntegracaoException(
            final Throwable cause,
            final String className,
            final EErrors erro) {
        super(erro.getDescricao());

        Logger.getLogger(className).log(
                Level.SEVERE,
                NestedExceptionUtils.buildMessage(erro.getDescricaoTecnica(), cause));
    }

    public IntegracaoException(
        final Throwable cause,
        final String className,
        final String erro) {
        super(erro);

        Logger.getLogger(className).log(Level.SEVERE,
            NestedExceptionUtils.buildMessage(erro, cause), cause);
    }

    public IntegracaoException(HystrixBadRequestException request) {
        if (request instanceof FeignBadResponseWrapper) {
            String message = tratarException((FeignBadResponseWrapper) request).get(0).getMessage();
            throw new IntegracaoException(message);
        }
    }

    private List<MessageException> tratarException(FeignBadResponseWrapper request) {
        List<MessageException> response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<MessageException>> typeReference = new TypeReference<List<MessageException>>() {
            };
            response = mapper.readValue(request.getBody(), typeReference);
        } catch (Exception ex) {
            throw new IntegracaoException(ex,
                    IntegracaoException.class.getName(),
                    EErrors.ERRO_CONVERTER_EXCEPTION);
        }
        return response;
    }
}
