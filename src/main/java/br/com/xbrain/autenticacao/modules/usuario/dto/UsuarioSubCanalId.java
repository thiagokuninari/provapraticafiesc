package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSubCanalId {

    private Integer id;
    private String nome;
    private Integer subCanalId;
}
