package br.com.xbrain.autenticacao.modules.site.enums;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento.COMERCIAL;

@NoArgsConstructor
public enum EHierarquiaSite {

    TODOS_VISUALIZAR_EDITAR(List.of(MSO_CONSULTOR, ADMINISTRADOR), List.of(COMERCIAL, CodigoDepartamento.ADMINISTRADOR)),
    VISUALIZAR_EDITAR_SUBORDINADOS(List.of(DIRETOR_OPERACAO, GERENTE_OPERACAO), List.of(COMERCIAL)),
    VISUALIZAR_DE_SUPERIORES(List.of(ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS), List.of(COMERCIAL)),
    VISUALIZAR_PROPRIO(List.of(COORDENADOR_OPERACAO, SUPERVISOR_OPERACAO), List.of(COMERCIAL)),
    NAO_AUTORIZADO;

    @Getter
    private List<CodigoCargo> cargos;
    @Getter
    private List<CodigoDepartamento> codigoDepartamento;

    EHierarquiaSite(List<CodigoCargo> cargos, List<CodigoDepartamento> codigoDepartamento) {
        this.cargos = cargos;
        this.codigoDepartamento = codigoDepartamento;
    }

    public static EHierarquiaSite getHierarquia(CodigoCargo codigoCargo, CodigoDepartamento codigoDepartamento) {
        return Stream.of(EHierarquiaSite.values())
            .filter(exclude -> !exclude.equals(NAO_AUTORIZADO))
            .filter(eHierarquia -> eHierarquia.getCargos().contains(codigoCargo)
                && eHierarquia.codigoDepartamento.contains(codigoDepartamento))
            .map(eHierarquiaSite -> valueOf(eHierarquiaSite.name()))
            .findFirst()
            .orElse(NAO_AUTORIZADO);
    }
}
