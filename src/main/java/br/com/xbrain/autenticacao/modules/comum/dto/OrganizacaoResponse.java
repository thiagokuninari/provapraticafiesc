package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizacaoResponse {

    private Integer id;
    private String nome;
    private String codigo;

    public static OrganizacaoResponse of(Organizacao organizacao) {
        return OrganizacaoResponse.builder()
            .id(organizacao.getId())
            .nome(organizacao.getNome())
            .codigo(organizacao.getCodigo())
            .build();
    }
}
