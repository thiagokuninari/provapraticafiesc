package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CidadeUfResponse {

    private Integer cidadeId;
    private String cidade;
    private String uf;
    private String ufSigla;
    private Integer ufId;

    public static CidadeUfResponse of(Cidade cidade) {
        return CidadeUfResponse.builder()
            .cidadeId(cidade.getId())
            .cidade(cidade.getNome())
            .uf(cidade.getNomeUf())
            .ufSigla(cidade.getUf().getUf())
            .ufId(cidade.getIdUf())
            .build();
    }
}
