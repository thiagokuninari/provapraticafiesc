package br.com.xbrain.autenticacao.modules.feeder.service;

import java.util.List;

public class FeederUtil {

    public static final Integer FUNCIONALIDADE_GERENCIAR_LEAD_ID = 15000;
    public static final Integer FUNCIONALIDADE_TRATAR_LEAD_ID = 3046;
    public static final String OBSERVACAO_FEEDER =
        "AGENTE AUTORIZADO COM PERMISSÃO DE FEEDER.";
    public static final String OBSERVACAO_NAO_FEEDER =
        "AGENTE AUTORIZADO SEM PERMISSÃO DE FEEDER.";
    public static final List<Integer> FUNCIONALIDADES_FEEDER_PARA_AA = List.of(15000, 15005, 3046);

}
