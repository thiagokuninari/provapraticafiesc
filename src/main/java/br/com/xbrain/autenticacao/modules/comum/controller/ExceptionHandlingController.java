package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.MessageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidacaoException.class)
    @ResponseBody
    public List<MessageException> validacaoError(ValidacaoException ex) {
        return Arrays.asList(new MessageException(ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(PermissaoException.class)
    @ResponseBody
    public List<MessageException> permissaoError(PermissaoException ex) {
        return Arrays.asList(new MessageException("Usuário sem permissão sobre a entidade requisitada."));
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public List<MessageException> notFoundError(NotFoundException ex) {
        return Collections.singletonList(new MessageException(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<MessageException> argumentValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        return result.getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage().toLowerCase().contains("campo")
                        ? new MessageException(e.getDefaultMessage())
                        : new MessageException(e.getField(), "O campo " + e.getField() + " " + e.getDefaultMessage())
                ).collect(Collectors.toList());
    }
}
