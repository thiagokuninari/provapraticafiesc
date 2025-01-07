package br.com.xbrain.autenticacao.modules.canalnetsales.helper;

import br.com.xbrain.autenticacao.modules.canalnetsales.dto.CanalNetSalesResponse;

public class CanalNetSalesHelper {

    public static CanalNetSalesResponse umCanalNetSalesResponse() {
        return CanalNetSalesResponse.builder()
            .id(2)
            .codigo("migracao")
            .build();
    }
}
