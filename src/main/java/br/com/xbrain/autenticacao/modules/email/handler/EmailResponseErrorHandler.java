package br.com.xbrain.autenticacao.modules.email.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class EmailResponseErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(EmailResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse chr) throws IOException {
        if (chr.getStatusCode() != HttpStatus.OK) {
            logger.error("Erro ao enviar email Status code: " + chr.getStatusCode());
            return true;
        }
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse chr) throws IOException {
        if (chr.getStatusCode() != HttpStatus.OK) {
            logger.error("Erro ao enviar email Status code: " + chr.getStatusCode());
        }
    }
}
