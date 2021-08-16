package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Data
public class CargoResponse {

    private Integer id;
    private String nome;
    private String codigo;
    private Integer quantidadeSuperior;
    private boolean possuiCargoSuperior;
    private ESituacao situacao;
    private Integer nivel;
    private Set<ECanal> canais;

    public static CargoResponse of(Cargo cargo) {
        CargoResponse response = new CargoResponse();
        BeanUtils.copyProperties(cargo, response);
        response.setCodigo(!isNull(cargo.getCodigo())
            ? cargo.getCodigo().name()
            : null);
        response.setSituacao(cargo.getSituacao());
        response.setPossuiCargoSuperior(!isEmpty(cargo.getSuperiores()));
        response.setNivel(!isNull(cargo.getNivel()) ? cargo.getNivel().getId() : null);
        response.setCanais(cargo.getCanais());

        return response;
    }
}
