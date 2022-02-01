package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CidadeResponse {

    private Integer id;
    private String nome;
    private String codigoIbge;
    private UfResponse uf;
    private RegionalDto regional;
    private Eboolean netUno;

    public static CidadeResponse of(Cidade request) {
        var response = new CidadeResponse();
        BeanUtils.copyProperties(request, response);
        response.setUf(UfResponse.parse(request.getUf()));
        response.setRegional(RegionalDto.of(request.getRegional()));
        return response;
    }
}