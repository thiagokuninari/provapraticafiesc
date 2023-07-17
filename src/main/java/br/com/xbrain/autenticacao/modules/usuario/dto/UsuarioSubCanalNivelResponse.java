package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSubCanalNivelResponse {

    private Integer id;
    private String nome;
    private CodigoNivel nivel;
    private Set<SubCanalDto> subCanais;

    public static UsuarioSubCanalNivelResponse of(Usuario usuario) {

        return UsuarioSubCanalNivelResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .nivel(usuario.getNivelCodigo())
            .subCanais(SubCanalDto.of(usuario.getSubCanais()))
            .build();
    }
}

