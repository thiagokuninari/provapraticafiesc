package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.helper;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;

public class UsuarioDtoVendasHelper {

    public static UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }
}
