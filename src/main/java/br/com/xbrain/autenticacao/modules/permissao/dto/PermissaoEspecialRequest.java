package br.com.xbrain.autenticacao.modules.permissao.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PermissaoEspecialRequest {

    @NotNull
    private Integer usuarioId;
    @NotEmpty
    private List<Integer> funcionalidadesIds;

    public PermissaoEspecialRequest() {}

    public PermissaoEspecialRequest(Integer usuarioId,
                                    List<Integer> funcionalidadesIds) {
        this.usuarioId = usuarioId;
        this.funcionalidadesIds = funcionalidadesIds;
    }
}
