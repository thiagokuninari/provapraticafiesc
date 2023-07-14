package br.com.xbrain.autenticacao.modules.usuario.exceptions;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;

import java.util.ArrayList;
import java.util.List;

public class ValidacaoSubCanalException extends RuntimeException {

    private List<UsuarioSubCanalDto> usuarios = new ArrayList<>();

    public ValidacaoSubCanalException(String message) {
        super(message);
    }

    public void adicionarUsuario(UsuarioSubCanalDto usuarioSubCanalDto) {
        this.usuarios.add(usuarioSubCanalDto);
    }
}

