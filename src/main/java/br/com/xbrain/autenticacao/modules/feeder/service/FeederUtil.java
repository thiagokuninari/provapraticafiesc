package br.com.xbrain.autenticacao.modules.feeder.service;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;

public class FeederUtil {

    public static final List<CodigoCargo> CODIGOS_CARGOS_VENDEDORES_FEEDER_E_SOCIO_PRINCIPAL =
        List.of(AGENTE_AUTORIZADO_VENDEDOR_D2D, AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D, AGENTE_AUTORIZADO_SOCIO);
    public static final List<CodigoCargo> CODIGOS_CARGOS_VENDEDORES_FEEDER =
        List.of(AGENTE_AUTORIZADO_VENDEDOR_D2D, AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D);
}
