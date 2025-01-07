package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoCidadeResponse {

    private Integer id;
    private String nome;
    private String uf;

    public static ConfiguracaoCidadeResponse of(Cidade cidade) {
        return ConfiguracaoCidadeResponse.builder()
            .id(cidade.getId())
            .nome(cidade.getNome())
            .uf(cidade.getUf().getUf())
            .build();
    }
}
