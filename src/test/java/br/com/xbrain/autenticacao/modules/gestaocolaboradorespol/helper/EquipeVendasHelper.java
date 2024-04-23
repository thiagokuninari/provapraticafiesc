package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.helper;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;

public class EquipeVendasHelper {

    public static EquipeVendasSupervisionadasResponse umaEquipeVendasSupervisionadasResponse() {
        return EquipeVendasSupervisionadasResponse
            .builder()
            .id(1)
            .canalVenda("D2D")
            .descricao("Equipe Teste")
            .build();
    }

    public static EquipeVendaDto umaEquipeVendaDto() {
        return EquipeVendaDto
            .builder()
            .id(1)
            .canalVenda("TELEVENDAS")
            .descricao("Equipe Teste 2")
            .build();
    }
}
