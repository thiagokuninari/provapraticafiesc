package br.com.xbrain.autenticacao.modules.usuario.exceptions;

import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class SubCanalCustomExceptionHandler {

    private final UsuarioSubCanalObserver usuarioSubCanalObserver;

    @ExceptionHandler(ValidacaoSubCanalException.class)
    public ResponseEntity<ValidacaoSubCanalException> validacaoSubCanalDosSubordinados(ValidacaoSubCanalException ex) {
        var exception = new ValidacaoSubCanalException(ex.getMessage());
        usuarioSubCanalObserver.getUsuariosComSubCanais().forEach(usuarioComSubCanal -> {
            exception.adicionarUsuario(usuarioComSubCanal);
        });
        deletarEvento();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    private void deletarEvento() {
        usuarioSubCanalObserver.getUsuariosComSubCanais().clear();
    }
}

