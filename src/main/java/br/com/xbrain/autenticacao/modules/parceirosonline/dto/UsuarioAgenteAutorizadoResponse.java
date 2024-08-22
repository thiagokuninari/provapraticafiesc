package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAgenteAutorizadoResponse {

    private Integer id;
    private String nome;
    private String email;
    private Integer equipeVendaId;
    private Integer agenteAutorizadoId;

    public UsuarioAgenteAutorizadoResponse(Integer id, String nome, Integer equipeVendaId) {
        this.id = id;
        this.nome = nome;
        this.equipeVendaId = equipeVendaId;
    }

    public static UsuarioAgenteAutorizadoResponse of(Usuario usuario) {
        var usuarioResponse = new UsuarioAgenteAutorizadoResponse();
        BeanUtils.copyProperties(usuario, usuarioResponse);
        return usuarioResponse;
    }
}
