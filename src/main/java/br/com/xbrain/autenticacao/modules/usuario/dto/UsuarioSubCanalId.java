package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsuarioSubCanalId {

    private String nome;
    private Integer subCanalId;

    public static UsuarioSubCanalId of(String nome, Integer subCanalId) {
        return new UsuarioSubCanalId(nome, subCanalId);
    }
}
