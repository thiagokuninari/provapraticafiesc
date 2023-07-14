package br.com.xbrain.autenticacao.modules.usuario.event;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UsuarioSubCanalEvent extends ApplicationEvent {

    private List<UsuarioSubCanalDto> usuarios;

    public UsuarioSubCanalEvent(Object source,
                                List<UsuarioSubCanalId> usuariosComSubCanalId) {
        super(source);
        this.usuarios = usuariosComSubCanalId.stream()
            .map(UsuarioSubCanalDto::of)
            .collect(Collectors.toList());
    }
}
