package br.com.xbrain.autenticacao.modules.usuario.helpers;

import java.util.List;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;

public class SubCanalHelper {

    public static List<SubCanal> umaListaSubCanais() {
        return List.of(umSubCanal(), doisSubCanal());
    }
    
    public static SubCanal umSubCanal() {
        return SubCanal.builder()
            .id(1)
            .codigo("PAP")
            .nome("PAP")
            .situacao(ESituacao.A)
            .build();
    }

    public static SubCanal doisSubCanal() {
        return SubCanal.builder()
            .id(2)
            .codigo("PAP_PME")
            .nome("PAP PME")
            .situacao(ESituacao.A)
            .build();
    }
}
