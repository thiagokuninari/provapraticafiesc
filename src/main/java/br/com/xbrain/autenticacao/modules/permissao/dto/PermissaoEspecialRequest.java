package br.com.xbrain.autenticacao.modules.permissao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissaoEspecialRequest {

    @NotNull
    private Integer usuarioId;
    @NotEmpty
    private List<Integer> funcionalidadesIds;

}
