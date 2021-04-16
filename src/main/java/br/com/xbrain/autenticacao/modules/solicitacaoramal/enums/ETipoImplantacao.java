package br.com.xbrain.autenticacao.modules.solicitacaoramal.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ETipoImplantacao {

    ESCRITORIO("ESCRITORIO", "ESCRITÓRIO"),
    HOME_OFFICE("HOME_OFFICE", "HOME OFFICE");

    @Getter
    private String codigo;
    @Getter
    private String descricao;

    ETipoImplantacao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

}
