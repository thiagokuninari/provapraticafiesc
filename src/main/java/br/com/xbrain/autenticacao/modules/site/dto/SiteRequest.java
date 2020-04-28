package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteRequest {

    private Integer id;
    @NotNull
    private String nome;
    @NotNull
    private ETimeZone timeZone;
    @NotEmpty
    private List<Integer> coordenadoresIds;
    @NotEmpty
    private List<Integer> supervisoresIds;
    @NotEmpty
    private List<Integer> estadosIds;
    private List<Integer> cidadesIds;
    private boolean incluirCidadesDisponiveis;
}
