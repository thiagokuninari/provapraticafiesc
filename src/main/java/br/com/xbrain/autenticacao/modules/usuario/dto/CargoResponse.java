package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
public class CargoResponse {

    private Integer id;
    private String nome;
    private Integer nivel;
    private String codigo;
    private ESituacao situacao;
    private Integer cargoSuperiorId;
    private String cargoSuperiorNome;
    private CodigoCargo cargoSuperiorCodigo;

    public CargoResponse() { }

    public CargoResponse(Cargo cargo) {
        BeanUtils.copyProperties(convertFrom(cargo), this);
    }

    public static List<CargoResponse> convertFrom(List<Cargo> cargos) {
        return !CollectionUtils.isEmpty(cargos)
                ? cargos.stream().map(CargoResponse::convertFrom).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public static CargoResponse convertFrom(Cargo cargo) {
        CargoResponse response = new CargoResponse();
        BeanUtils.copyProperties(cargo, response);
        response.setNivel(!isEmpty(cargo.getNivel()) ? new Nivel(cargo.getNivel().getId()).getId() : null);
        response.setSituacao(!isEmpty(cargo.getSituacao()) ? cargo.getSituacao() : null);
        response.setCodigo(!isEmpty(cargo.getCodigo()) ? cargo.getCodigo().name() : null);
        response.setCargoSuperiorId(!isEmpty(cargo.getCargoSuperior()) ? cargo.getCargoSuperior().getId() : null);
        response.setCargoSuperiorNome(!isEmpty(cargo.getCargoSuperior()) ? cargo.getCargoSuperior().getNome() : null);
        response.setCargoSuperiorCodigo(!isEmpty(cargo.getCargoSuperior()) ? cargo.getCargoSuperior().getCodigo() : null);
        return response;
    }
}
