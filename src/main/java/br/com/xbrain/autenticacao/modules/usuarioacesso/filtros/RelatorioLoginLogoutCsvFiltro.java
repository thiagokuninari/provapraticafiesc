package br.com.xbrain.autenticacao.modules.usuarioacesso.filtros;

import br.com.xbrain.autenticacao.modules.usuarioacesso.predicate.UsuarioAcessoPredicate;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

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

    public BooleanBuilder toPredicate() {
        return new UsuarioAcessoPredicate()
            .porUsuarioIds(colaboradoresIds)
            .porPeriodoDataCadastro(dataInicio, dataFim)
            .build();
    }
}
