package br.com.xbrain.autenticacao.modules.usuario.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodigoNivel {
    AGENTE_AUTORIZADO(false),
    AGENTE_AUTORIZADO_NACIONAL(true),
    MSO(false),
    OPERACAO(true),
    XBRAIN(false),
    VAREJO(true),
    ATP(true),
    LOJAS(true),
    RECEPTIVO(true),
    RECEPTIVO_SEGMENTADO(true),
    ATIVO_LOCAL_PROPRIO(true),
    ATIVO_LOCAL_TERCEIRO(true),
    ATIVO_NACIONAL_TERCEIRO(true),
    ATIVO_NACIONAL_TERCEIRO_SEGMENTADO(true),
    ATIVO_RENTABILIZACAO(true),
    ATIVO_LOCAL_COLABORADOR(true),
    BACKOFFICE(false),
    BACKOFFICE_CENTRALIZADO(false),
    OUVIDORIA(true),
    INTEGRACAO(false),
    GERADOR_LEADS(false),
    FEEDER(false),
    COBRANCA(true),
    BACKOFFICE_SUPORTE_VENDAS(false),
    BRIEFING(false);

    private final boolean dadosNetSalesObrigatorios;

    public static boolean isNivelObrigatorioDadosNetSales(CodigoNivel codigoNivel) {
        return Arrays.stream(CodigoNivel.values())
            .filter(CodigoNivel::isDadosNetSalesObrigatorios)
            .anyMatch(nivel -> nivel.equals(codigoNivel));
    }
}
