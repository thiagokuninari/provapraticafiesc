package br.com.xbrain.autenticacao.modules.comum.exception;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.model.MessageException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.apache.commons.lang3.StringUtils;
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
            var message = tratarException((FeignBadResponseWrapper) request).getMessage();
            throw new IntegracaoException(message);
        }
    }

    private MessageException tratarException(FeignBadResponseWrapper request) {
        try {
            var mapper = new ObjectMapper();
            var typeReference = new TypeReference<MessageException>() {
            };
            var typeReferenceList = new TypeReference<List<MessageException>>() {
            };

            if (StringUtils.isNotBlank(request.getBody()) && request.getBody().contains("[")) {
                return ((List<MessageException>) mapper.readValue(request.getBody(), typeReferenceList)).get(0);
            }

            return mapper.readValue(request.getBody(), typeReference);
        } catch (Exception ex) {
            throw new IntegracaoException(ex,
                IntegracaoException.class.getName(),
                EErrors.ERRO_CONVERTER_EXCEPTION);
        }
    }
}
