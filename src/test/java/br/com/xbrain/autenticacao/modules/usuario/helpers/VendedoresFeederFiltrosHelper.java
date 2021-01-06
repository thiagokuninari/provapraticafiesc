package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.dto.VendedoresFeederFiltros;

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
