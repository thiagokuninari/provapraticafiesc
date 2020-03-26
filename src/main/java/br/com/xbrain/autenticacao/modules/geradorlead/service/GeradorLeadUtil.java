package br.com.xbrain.autenticacao.modules.geradorlead.service;

import java.util.List;

public class GeradorLeadUtil {

    public static final Integer FUNCIONALIDADE_GERENCIAR_LEAD_ID = 15000;
    public static final Integer FUNCIONALIDADE_TRATAR_LEAD_ID = 3046;
    public static final String OBSERVACAO_GERADOR_LEADS =
        "AGENTE AUTORIZADO SE TORNOU GERADOR DE LEADS";
    public static final String OBSERVACAO_NAO_GERADOR_LEADS =
        "AGENTE AUTORIZADO NÃO É GERADOR DE LEADS";
    public static final List<Integer> FUNCIONALIDADES_GERADOR_LEADS_PARA_AA = List.of(15000, 15001, 15002, 15005, 3046);

}
