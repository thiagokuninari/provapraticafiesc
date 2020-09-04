package br.com.xbrain.autenticacao.modules.usuarioacesso.filtros;

import br.com.xbrain.xbrainutils.DateUtils;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioLoginLogoutCsvFiltro {

    @NotEmpty
    private Set<Integer> colaboradoresIds;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    public Map<String, String> toFeignRequestMap(Collection<Integer> usuariosIdsPermitidos) {
        var map = Maps.<String, String>newHashMap();
        map.put("usuariosIds", colaboradoresIds.stream()
            .filter(usuariosIdsPermitidos::contains)
            .map(String::valueOf)
            .collect(Collectors.joining(",")));
        map.put("dataInicio", DateUtils.parseLocalDateToString(dataInicio));
        map.put("dataFim", DateUtils.parseLocalDateToString(dataFim));
        return map;
    }
}
