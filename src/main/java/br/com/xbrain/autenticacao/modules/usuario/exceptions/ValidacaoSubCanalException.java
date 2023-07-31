package br.com.xbrain.autenticacao.modules.usuario.exceptions;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;

import java.util.ArrayList;
import java.util.List;

public class ValidacaoSubCanalException extends RuntimeException {

    private List<UsuarioSubCanalDto> errors = new ArrayList<>();

    public ValidacaoSubCanalException(String message) {
        super(message);
    }

    public List<UsuarioSubCanalDto> getErrors() {
        return errors;
    }

    public void adicionarUsuario(UsuarioSubCanalDto usuarioSubCanalDto) {
        this.errors.add(usuarioSubCanalDto);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

