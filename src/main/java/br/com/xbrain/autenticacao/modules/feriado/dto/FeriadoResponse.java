package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Integer fkCidade;
    private String cidadePai;
    private Integer estadoId;
    private String estadoNome;
    private ETipoFeriado tipoFeriado;
    private Integer anoReferencia;

    public FeriadoResponse(LocalDate dataFeriado) {
        this.dataFeriado = dataFeriado;
    }

    public static FeriadoResponse of(Feriado feriado) {
        var response = new FeriadoResponse();
        BeanUtils.copyProperties(feriado, response);
        if (nonNull(feriado.getCidade())) {
            response.setCidadeId(feriado.getCidade().getId());
            response.setCidadeNome(feriado.getCidade().getNome());
            response.setFkCidade(feriado.getCidade().getFkCidade());
        }
        if (nonNull(feriado.getUf())) {
            response.setEstadoId(feriado.getUf().getId());
            response.setEstadoNome(feriado.getUf().getNome());
        }
        response.setAnoReferencia(feriado.getDataFeriado().getYear());
        return response;
    }

    public static FeriadoResponse definirNomeCidadePaiPorDistritos(FeriadoResponse feriadoResponse,
                                                                   Map<Integer, CidadeResponse> distritos) {
        if (distritos.containsKey(feriadoResponse.cidadeId)) {
            feriadoResponse.setCidadePai(distritos.get(feriadoResponse.cidadeId).getCidadePai());
        }

        return feriadoResponse;
    }
}
