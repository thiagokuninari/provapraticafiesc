package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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
public class UsuarioSubCanalResponse {

    private Integer id;
    private String nome;
    private String cpf;
    private ESituacao situacao;
    private CodigoNivel codigoNivel;
    private Set<ECanal> canais;
    private CodigoCargo codigoCargo;
    private Set<SubCanalDto> subCanais;

    public static UsuarioSubCanalResponse of(Usuario usuario) {
        return UsuarioSubCanalResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .cpf(usuario.getCpf())
            .situacao(usuario.getSituacao())
            .codigoNivel(usuario.getNivelCodigo())
            .canais(usuario.hasCanal(ECanal.D2D_PROPRIO) ? Set.of(ECanal.D2D_PROPRIO) : null)
            .codigoCargo(usuario.getCargoCodigo())
            .subCanais(SubCanalDto.of(usuario.getSubCanais()))
            .build();
    }
}

