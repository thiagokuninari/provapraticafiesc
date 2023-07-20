package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.*;

import static br.com.xbrain.autenticacao.modules.usuario.util.UsuarioConstantesUtils.POSICAO_UM;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsuarioSubCanalDto {

    private Integer id;
    private String nome;
    private ETipoCanal subCanal;

    public static UsuarioSubCanalDto of(UsuarioSubCanalId usuarioSubCanalId) {
        return UsuarioSubCanalDto.builder()
            .id(usuarioSubCanalId.getId())
            .nome(usuarioSubCanalId.getNome())
            .subCanal(getETipoCanal(usuarioSubCanalId.getSubCanalId()))
            .build();
    }

    private static ETipoCanal getETipoCanal(Integer subCanalId) {
        return ETipoCanal.values()[subCanalId - POSICAO_UM];
    }
}
