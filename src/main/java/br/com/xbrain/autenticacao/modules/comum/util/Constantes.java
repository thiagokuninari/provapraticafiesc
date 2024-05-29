package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Constantes {

    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final Integer QTD_MAX_IN_NO_ORACLE = 1000;
    public static final String VIRGULA = ",";
    public static final String TEST = "test";
    public static final int INDICE_ZERO = 0;
    public static final int ROLE_SHB = 30000;
    public static final List<CodigoCargo> CARGOS_HIBRIDOS_PERMITIDOS = List.of(
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR,
        CodigoCargo.AGENTE_AUTORIZADO_ACEITE
    );
    public static final List<String> PERMISSOES_DE_VENDA = List.of(
        "VDS_TABULACAO_MANUAL",
        "VDS_TABULACAO_DISCADORA",
        "VDS_TABULACAO_CLICKTOCALL",
        "VDS_TABULACAO_PERSONALIZADA"
    );
    public static final List<CodigoCargo> CARGOS_SUPERVISOR = List.of(
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_TEMP
    );
    public static final List<CodigoCargo> LISTA_CARGOS_SUPERIORES_AGENTE_AUTORIZADO = List.of(
        CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_GERENTE_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO,
        CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_TEMP,
        CodigoCargo.AGENTE_AUTORIZADO_TECNICO_COORDENADOR,
        CodigoCargo.AGENTE_AUTORIZADO_TECNICO_GERENTE,
        CodigoCargo.AGENTE_AUTORIZADO_TECNICO_SUPERVISOR);
    public static final Integer PERMISSAO_DESBLOQUEAR_INDICACAO_EXTERNA_ID = 22127;

}
