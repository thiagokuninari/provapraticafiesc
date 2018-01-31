package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FuncionalidadeSaveRequest {

    @NotNull
    private Integer empresaId;

    @NotNull
    private Integer departamentoId;

    @NotNull
    private Integer cargoId;

    @NotEmpty
    private List<Integer> funcionalidadesIds;

    public FuncionalidadeSaveRequest() {}

    public FuncionalidadeSaveRequest(Integer empresaId,
                                     Integer departamentoId,
                                     Integer cargoId,
                                     List<Integer> funcionalidadesIds) {
        this.empresaId = empresaId;
        this.departamentoId = departamentoId;
        this.cargoId = cargoId;
        this.funcionalidadesIds = funcionalidadesIds;
    }
}
