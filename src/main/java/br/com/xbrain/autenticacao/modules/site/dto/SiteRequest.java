package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteRequest {

    private Integer id;
    @NotNull
    private String nome;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
