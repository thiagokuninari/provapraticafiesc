package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CidadeUfResponse {

    private Integer cidadeId;
    private String cidade;
    private String uf;
    private Integer ufId;

    public static CidadeUfResponse of(Cidade cidade) {
        return CidadeUfResponse.builder()
            .cidadeId(cidade.getId())
            .cidade(cidade.getNome())
            .uf(cidade.getNomeUf())
            .ufId(cidade.getIdUf())
            .build();
    }

    @JsonIgnore
    public String getNomeComUf() {
        return Objects.nonNull(uf) ? cidade + " - " + uf : cidade;
    }
}
