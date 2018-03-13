package br.com.xbrain.autenticacao.modules.permissao.filtros;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;

@Data
public class CargoDepartamentoFuncionalidadeFiltros {

    private Integer departamentoId;
    private Integer cargoId;

    @JsonIgnore
    public BooleanBuilder toPredicate() {
        return new CargoDepartamentoFuncionalidadePredicate()
                .comCargo(this.cargoId)
                .comDepartamento(this.departamentoId)
                .build();
    }
}
