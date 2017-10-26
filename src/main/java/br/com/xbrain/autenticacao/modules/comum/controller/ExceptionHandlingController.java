package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidacaoException.class)
    @ResponseBody
    public List<Message> validacaoError(ValidacaoException ex) {
        return Arrays.asList(new Message(ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(PermissaoException.class)
    @ResponseBody
    public List<Message> permissaoError(PermissaoException ex) {
        return Arrays.asList(new Message("Usuário sem permissão sobre a entidade requisitada."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<Message> argumentValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        return result.getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage().toLowerCase().contains("campo")
                        ? new Message(e.getDefaultMessage())
                        : new Message(e.getField(), "O campo " + e.getField() + " " + e.getDefaultMessage())
                ).collect(Collectors.toList());
    }

    @Data
    private class Message {
        private String message;
        private String field;

        public Message(String message) {
            this.message = message;
        }

        public Message(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
