package br.com.xbrain.autenticacao.modules.permissao.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CargoDepartamentoFuncionalidadeRequest {

    @NotNull
    private Integer departamentoId;
    @NotNull
    private Integer cargoId;
    @NotEmpty
    private List<Integer> funcionalidadesIds;

    public CargoDepartamentoFuncionalidadeRequest() {
    }

    public CargoDepartamentoFuncionalidadeRequest(Integer departamentoId,
                                                  Integer cargoId,
                                                  List<Integer> funcionalidadesIds) {
        this.departamentoId = departamentoId;
        this.cargoId = cargoId;
        this.funcionalidadesIds = funcionalidadesIds;
    }
}
