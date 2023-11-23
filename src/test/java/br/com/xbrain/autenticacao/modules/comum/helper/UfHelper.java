package br.com.xbrain.autenticacao.modules.comum.helper;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.dto.UfResponse;

import java.util.List;

public class UfHelper {

    public static Uf ufParana() {
        return Uf.builder()
            .id(1)
            .nome("PARANA")
            .uf("PR")
            .regionais(List.of(RegionalHelper.novaRegionalRps()))
            .build();
    }

    public static Uf ufSaoPaulo() {
        return Uf.builder()
            .id(2)
            .nome("SAO PAULO")
            .uf("SP")
            .regionais(List.of(RegionalHelper.novaRegionalRsi()))
            .build();
    }

    public static Uf ufSantaCatarina() {
        return Uf.builder()
            .id(22)
            .nome("SANTA CATARINA")
            .uf("SC")
            .regionais(List.of(RegionalHelper.novaRegionalRps()))
            .build();
    }

    public static UfResponse ufResponseParana() {
        return UfResponse.parse(ufParana());
    }

    public static UfResponse ufResponseSaoPaulo() {
        return UfResponse.parse(ufSaoPaulo());
    }
}
