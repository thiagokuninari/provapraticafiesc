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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.DateUtil.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelatorioLoginLogoutFiltros {

    private static final int PERIODO_TRINTA_DIAS = 30;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicial;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;
    @NotEmpty
    private List<Integer> usuariosOperadoresIds;

    public Map<String, Object> toRelatorioLoginLogoutMap(List<Integer> usuariosIdsPart) {
        var map = Maps.<String, Object>newHashMap();
        map.put("usuariosIds", usuariosIdsPart.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",")));
        map.put("dataInicio", DateUtils.parseLocalDateToString(dataInicial));
        map.put("dataFim", DateUtils.parseLocalDateToString(dataFinal));
        return map;
    }

    public void validarDatas() {
        validarPeriodoMaximo(
            DateUtils.parseLocalDateToString(dataInicial),
            DateUtils.parseLocalDateToString(dataFinal),
            PERIODO_TRINTA_DIAS);
        validarDataInicialPosteriorDataFinal(dataInicial, dataFinal);
        validarDataFinalPosteriorAAtual(dataFinal);
    }
}
