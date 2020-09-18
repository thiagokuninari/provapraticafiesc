package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ESituacao situacao;

    public static UsuarioNomeResponse of(Integer id, String nome, ESituacao situacao) {
        return builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }
}
