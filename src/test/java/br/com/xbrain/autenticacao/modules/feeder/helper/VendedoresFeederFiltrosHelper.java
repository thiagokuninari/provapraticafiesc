package br.com.xbrain.autenticacao.modules.feeder.helper;

import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederFiltros;

import java.util.List;

public class VendedoresFeederFiltrosHelper {

    public static VendedoresFeederFiltros umVendedoresFeederFiltros(List<Integer> aasIds,
                                                                    Boolean comSocioPrincipal,
                                                                    Boolean buscarInativos) {
        return VendedoresFeederFiltros
            .builder()
            .aasIds(aasIds)
            .comSocioPrincipal(comSocioPrincipal)
            .buscarInativos(buscarInativos)
            .build();
    }
}
