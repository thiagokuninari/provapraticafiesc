package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CidadeResponse {

    private Integer id;
    private String nome;
    private UfResponse uf;

    public static CidadeResponse parse(Cidade request) {
        CidadeResponse response = new CidadeResponse();
        BeanUtils.copyProperties(request, response);
        response.setUf(UfResponse.parse(request.getUf()));
        return response;
    }
}