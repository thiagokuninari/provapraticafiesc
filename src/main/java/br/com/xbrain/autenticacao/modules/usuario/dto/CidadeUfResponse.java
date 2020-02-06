package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.Data;

@Data
public class CidadeUfResponse {

    private Integer ufId;
    private Integer cidadeId;

    public static CidadeUfResponse of(Cidade cidade) {
        var response = new CidadeUfResponse();
        response.setUfId(cidade.getIdUf());
        response.setCidadeId(cidade.getId());

        return response;
    }
}
