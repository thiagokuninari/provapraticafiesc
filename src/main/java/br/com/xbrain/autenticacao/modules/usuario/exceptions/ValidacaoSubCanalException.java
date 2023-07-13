package br.com.xbrain.autenticacao.modules.usuario.exceptions;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;

import java.util.ArrayList;
import java.util.List;

public class ValidacaoSubCanalException extends RuntimeException {

    private List<UsuarioSubCanalDto> usuarios = new ArrayList<>();

    public ValidacaoSubCanalException(String message) {
        super(message);
    }

    public void adicionarUsuario(String nomeUsuario, ETipoCanal subCanal) {
        this.usuarios.add(UsuarioSubCanalDto.of(nomeUsuario, subCanal));
    }
}

