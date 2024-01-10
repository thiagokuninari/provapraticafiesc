package br.com.xbrain.autenticacao.modules.comum.helper;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;

import java.util.List;

public class RegionalHelper {

    public static List<Integer> listaNovasRegionaisIds() {
        return List.of(1022, 1023, 1024, 1025, 1026, 1027, 1028, 1029, 1030, 1031);
    }

    public static Regional novaRegionalRps() {
        return Regional.builder()
            .id(1027)
            .nome("RPS")
            .situacao(ESituacao.A)
            .novaRegional(Eboolean.V)
            .build();
    }

    public static Regional novaRegionalRsc() {
        return Regional.builder()
            .id(1030)
            .nome("RSC")
            .situacao(ESituacao.A)
            .novaRegional(Eboolean.V)
            .build();
    }

    public static Regional novaRegionalRsi() {
        return Regional.builder()
            .id(1031)
            .nome("RSI")
            .situacao(ESituacao.A)
            .novaRegional(Eboolean.V)
            .build();
    }

    public static RegionalDto umaRegionalDto() {
        return RegionalDto.builder()
            .id(1)
            .nome("LESTE")
            .situacao(ESituacao.A)
            .build();
    }
}
