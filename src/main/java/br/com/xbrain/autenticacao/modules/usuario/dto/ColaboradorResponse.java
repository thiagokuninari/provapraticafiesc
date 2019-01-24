package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorResponse implements Serializable {

    private Integer id;
    private String nome;
    private String email;
    private Integer equipeVendaId;
    private String nomeCargo;

    private static ColaboradorResponse convertFrom(UsuarioSubordinadoDto usuarioSubordinado) {
        ColaboradorResponse response = new ColaboradorResponse();
        BeanUtils.copyProperties(usuarioSubordinado, response);
        return response;
    }

    public static List<ColaboradorResponse> convertFrom(List<UsuarioSubordinadoDto> usuarios) {
        return usuarios.stream().map(ColaboradorResponse::convertFrom).collect(Collectors.toList());
    }

    public static ColaboradorResponse convertFrom(Usuario usuario) {
        ColaboradorResponse response = new ColaboradorResponse();
        BeanUtils.copyProperties(usuario, response);
        response.setNomeCargo(usuario.getCargo().getNome());
        return response;
    }

    public ColaboradorResponse(Integer id, String nome, String email, String nomeCargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nomeCargo = nomeCargo;
    }
}

