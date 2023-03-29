package br.com.xbrain.autenticacao.config;

import lombok.Getter;

public enum EScopes {

    APP("app"),
    AUTENTICACAO("autenticacao-api"),
    PARCEIROS_ONLINE("parceiros-api"),
    VENDAS("vendas-api"),
    INTEGRACAO_VENDAS("integracao-vendas-api"),
    INTEGRACAO_BRSCAN("integracao-brscan-api"),
    MAILING("mailing-api"),
    MAILING_DISCADORA("mailing-discadora-api"),
    MAILING_IMPORTACAO("mailing-importacao-api"),
    EQUIPE_VENDA("equipevenda-api"),
    CALL("call-api"),
    DASHBOARD("dashboard-api"),
    DISCADORA_ECCP("discadora-eccp-api"),
    CONTATO_CRN("contato-crn-api"),
    CHAMADO("chamado-api"),
    FUNIL_PROSPECCAO("funil-prospeccao-api"),
    DISCADORA("discadora-api"),
    ASTERISK_URA("asterisk-ura-api"),
    CLICK_TO_CALL("click-to-call-api"),
    INDICACAO("indicacao-api"),
    GERADOR_LEAD("gerador-lead-api"),
    CHATBOT("chatbot-api"),
    SOLICITACAO_PAP("solicitacao-pap"),
    CLARO_INDICO("claro-indico-api"),
    BLOQUEIO_LIGACAO_API("bloqueio-ligacao-api");

    @Getter
    private String scope;

    EScopes(String scope) {
        this.scope = scope;
    }
}
