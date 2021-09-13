package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;

public class CargoHelper {

    public static Cargo umCargo(Integer id, CodigoCargo codigoCargo) {
        return Cargo
            .builder()
            .id(id)
            .codigo(codigoCargo)
            .build();
    }
}
