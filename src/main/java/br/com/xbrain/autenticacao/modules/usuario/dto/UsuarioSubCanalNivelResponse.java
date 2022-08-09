package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UsuarioSubCanalNivelResponse {

    private Integer id;
    private String nome;
    private CodigoNivel nivel;
    private List<SubCanalDto> subCanais;

    public static UsuarioSubCanalNivelResponse of(Usuario usuario) {
        var subCanais = usuario.getSubCanais()
            .stream()
            .collect(Collectors.toList());

        return UsuarioSubCanalNivelResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .nivel(usuario.getNivelCodigo())
            .subCanais(SubCanalDto.of(subCanais))
            .build();
    }
}

