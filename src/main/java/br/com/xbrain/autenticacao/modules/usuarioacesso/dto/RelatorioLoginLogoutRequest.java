package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.util.DateUtil.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelatorioLoginLogoutRequest {

    private static final int PERIODO_TRINTA_DIAS = 30;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicial;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;
    @NotEmpty
    private List<Integer> usuariosIds;

    public void validarDatas() {
        validarDataInicialPosteriorDataFinal(dataInicial, dataFinal);
        validarPeriodoMaximo(dataInicial, dataFinal, PERIODO_TRINTA_DIAS);
        validarDataFinalPosteriorAAtual(dataFinal);
    }
}
