package br.com.xbrain.autenticacao.modules.usuario.event;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalId;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.util.UsuarioConstantesUtils.POSICAO_MENOS_UM;

@Getter
public class UsuarioSubCanalEvent extends ApplicationEvent {

    private List<UsuarioSubCanalDto> usuarios;

    public UsuarioSubCanalEvent(Object source,
                                List<UsuarioSubCanalId> usuariosComSubCanalId) {
        super(source);
        this.usuarios = usuariosComSubCanalId.stream()
            .map(usuario -> UsuarioSubCanalDto.of(usuario.getNomeUsuario(), getETipoCanal(usuario.getSubCanalId())))
            .collect(Collectors.toList());
    }

    private ETipoCanal getETipoCanal(Integer subCanalId) {
        return ETipoCanal.values()[subCanalId - POSICAO_MENOS_UM];
    }
}
