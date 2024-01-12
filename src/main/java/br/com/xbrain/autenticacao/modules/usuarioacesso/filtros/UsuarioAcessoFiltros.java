package br.com.xbrain.autenticacao.modules.usuarioacesso.filtros;

import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.predicate.UsuarioAcessoPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAcessoFiltros {

    private static final int PERIODO_MAXIMO_FILTRO = 30;

    private String nome;
    private String cpf;
    private String email;
    private Integer aaId;
    private List<Integer> agenteAutorizadosIds;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;
    @NotNull
    private ETipo tipo;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataInicial;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataFinal;
    private Integer organizacaoId;
    private List<CodigoCargo> cargos;
    private Integer nivelId;
    private Integer cargoId;
    private ECanal canal;
    private Integer subCanalId;

    @JsonIgnore
    public BooleanBuilder toPredicate() {
        return new UsuarioAcessoPredicate()
            .porNome(nome)
            .porCpf(retirarMascaraCpf(cpf))
            .porEmail(email)
            .porPeriodo(dataInicio, dataFim, tipo)
            .porAa(aaId, agenteAutorizadosIds)
            .porNivel(nivelId)
            .porCargo(cargoId)
            .porCanal(canal)
            .porSubCanal(subCanalId)
            .build();
    }

    public void validarPeriodoMaiorQue30Dias() {
        DateUtil.validarPeriodoMaximo(dataInicio.toString(), dataFim.toString(), PERIODO_MAXIMO_FILTRO);
    }

    private String retirarMascaraCpf(String cpfUsr) {
        var cpf = Optional.ofNullable(cpfUsr).orElse("");

        return cpf.replaceAll("[.-]", "");
    }
}
