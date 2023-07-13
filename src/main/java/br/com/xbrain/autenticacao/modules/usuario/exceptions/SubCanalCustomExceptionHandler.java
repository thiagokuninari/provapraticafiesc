package br.com.xbrain.autenticacao.modules.usuario.exceptions;

import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubCanalCustomExceptionHandler {

    @Autowired
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @ExceptionHandler(ValidacaoSubCanalException.class)
    public ResponseEntity<ValidacaoSubCanalException> validationErrors(ValidacaoSubCanalException ex) {
        var exception = new ValidacaoSubCanalException(ex.getMessage());
        usuarioSubCanalObserver.getUsuariosComSubCanais().forEach(usuarioComSubCanal -> {
            exception.adicionarUsuario(usuarioComSubCanal.getNomeUsuario(), usuarioComSubCanal.getSubCanal());
        });
        deletarEvento();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    private void deletarEvento() {
        usuarioSubCanalObserver.getUsuariosComSubCanais().clear();
    }
}

