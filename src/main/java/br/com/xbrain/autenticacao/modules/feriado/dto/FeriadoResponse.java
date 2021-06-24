package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FeriadoResponse {

    private Integer id;
    private String nome;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFeriado;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataCadastro;
    private Eboolean feriadoNacional;
    private Integer cidadeId;
    private String cidadeNome;
    private Integer estadoId;
    private String estadoNome;
    private ETipoFeriado tipoFeriado;
    private Integer anoReferencia;

    public static FeriadoResponse convertFrom(Feriado feriado) {
        FeriadoResponse feriadoResponse = new FeriadoResponse();
        BeanUtils.copyProperties(feriado, feriadoResponse);
        if (nonNull(feriado.getCidade())) {
            feriadoResponse.setCidadeId(feriado.getCidade().getId());
        }
        return feriadoResponse;
    }

    public static FeriadoResponse of(Feriado feriado) {
        var response = new FeriadoResponse();
        BeanUtils.copyProperties(feriado, response);
        if (nonNull(feriado.getCidade())) {
            response.setCidadeId(feriado.getCidade().getId());
            response.setCidadeNome(feriado.getCidade().getNome());
        }
        if (nonNull(feriado.getUf())) {
            response.setEstadoId(feriado.getUf().getId());
            response.setEstadoNome(feriado.getUf().getNome());
        }
        response.setAnoReferencia(feriado.getDataFeriado().getYear());
        return response;
    }
}
