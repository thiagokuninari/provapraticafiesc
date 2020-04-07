package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.Getter;

public enum ETimeZone {

    AMERICA_PORTO_ACRE("America/Porto_Acre", -5),
    AMERICA_RIO_BRANCO("America/Rio_Branco", -5),
    AMERICA_CAMPO_GRANDE("America/Campo_Grande", -4),
    AMERICA_MANAUS("America/Manaus", -4),
    AMERICA_CUIABA("America/Cuiaba", -4),
    AMERICA_RECIFE("America/Recife", -3),
    AMERICA_SAO_PAULO("America/Sao_Paulo", -3),
    AMERICA_MACEIO("America/Maceio", -3),
    AMERICA_NORONHA("America/Noronha", -2);

    @Getter
    private String codigo;
    @Getter
    private Integer gmt;

    ETimeZone(String codigo, Integer gmt) {
        this.codigo = codigo;
        this.gmt = gmt;
    }

    @Override
    public String toString() {
        return codigo + " GMT " + gmt.toString();
    }
}
