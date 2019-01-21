package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public static FeriadoResponse convertFrom(Feriado feriado) {
        FeriadoResponse feriadoResponse = new FeriadoResponse();
        BeanUtils.copyProperties(feriado, feriadoResponse);
        if (Objects.nonNull(feriado.getCidade())) {
            feriadoResponse.setCidadeId(feriado.getCidade().getId());
        }
        return feriadoResponse;
    }
}
