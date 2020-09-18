package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioNomeResponse {
    private Integer id;
    private String nome;

    public static UsuarioNomeResponse of(Integer id, String nome) {
        return builder()
            .id(id)
            .nome(nome)
            .build();
    }
}
