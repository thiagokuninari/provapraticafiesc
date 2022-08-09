package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;

public class SubCanalHelper {

    public static List<SubCanal> umaListaSubCanais() {
        return List.of(umSubCanal(), doisSubCanal());
    }

    public static SubCanal umSubCanal() {
        return SubCanal.builder()
            .id(1)
            .codigo(ETipoCanal.PAP)
            .nome("PAP")
            .situacao(A)
            .build();
    }

    public static SubCanal doisSubCanal() {
        return SubCanal.builder()
            .id(2)
            .codigo(ETipoCanal.PAP_PME)
            .nome("PAP PME")
            .situacao(A)
            .build();
    }

    public static SubCanalDto umSubCanalDto(Integer id, ETipoCanal codigo, String nome) {
        return SubCanalDto.builder()
            .id(id)
            .codigo(codigo)
            .nome(nome)
            .situacao(A)
            .build();
    }
}
