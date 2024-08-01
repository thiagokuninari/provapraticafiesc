package br.com.xbrain.autenticacao.modules.permissao.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class CargoDepartamentoFuncionalidadeRequest {

    @NotNull
    private Integer departamentoId;
    @NotNull
    private Integer cargoId;
    @NotEmpty
    private List<Integer> funcionalidadesIds;
}
