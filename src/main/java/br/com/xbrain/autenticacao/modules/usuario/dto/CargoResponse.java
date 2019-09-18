package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

@Data
public class CargoResponse {

    private Integer id;
    private String nome;
    private String codigo;
    private Integer quantidadeSuperior;
    private boolean possuiCargoSuperior;

    public static CargoResponse of(Cargo cargo) {
        CargoResponse response = new CargoResponse();
        BeanUtils.copyProperties(cargo, response);
        response.setCodigo(cargo.getCodigo().name());
        response.setPossuiCargoSuperior(!CollectionUtils.isEmpty(cargo.getSuperiores()));
        return response;
    }
}
