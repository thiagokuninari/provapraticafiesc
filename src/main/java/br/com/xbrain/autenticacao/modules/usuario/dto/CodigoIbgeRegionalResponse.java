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
public class CodigoIbgeRegionalResponse {

    private Integer cidadeId;
    private String codigoIbge;
    private Integer regionalId;
    private String regionalNome;

    public static CodigoIbgeRegionalResponse of(Cidade cidade) {
        var response = new CodigoIbgeRegionalResponse();
        response.setRegionalId(cidade.getRegionalId());
        response.setRegionalNome(cidade.getRegionalNome());
        return response;
    }
}
