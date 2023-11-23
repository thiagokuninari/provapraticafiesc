package br.com.xbrain.autenticacao.modules.usuario.event;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;
import lombok.Getter;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class UsuarioSubCanalObserver implements ApplicationListener<UsuarioSubCanalEvent> {

    private List<UsuarioSubCanalDto> usuariosComSubCanais = new ArrayList<>();

    @Override
    public void onApplicationEvent(UsuarioSubCanalEvent event) {
        this.usuariosComSubCanais.addAll(event.getUsuarios());
    }
}
