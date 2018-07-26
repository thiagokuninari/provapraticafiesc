package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
public class CargoResponse {

    private Integer id;
    private String nome;
    private Integer nivel;
    private String codigo;
    private ESituacao situacao;

    public CargoResponse() {

    }

    public CargoResponse(Cargo cargo) {
        BeanUtils.copyProperties(convertFrom(cargo), this);
    }

    public static CargoResponse convertFrom(Cargo model) {
        CargoResponse response = new CargoResponse();
        BeanUtils.copyProperties(model, response);

        response.setNivel(!isEmpty(model.getNivel()) ? new Nivel(model.getNivel().getId()).getId() : null);
        response.setSituacao(!isEmpty(model.getSituacao()) ? model.getSituacao() : null);
        response.setCodigo(!isEmpty(model.getCodigo()) ? model.getCodigo().name() : null);

        return response;
    }
}
