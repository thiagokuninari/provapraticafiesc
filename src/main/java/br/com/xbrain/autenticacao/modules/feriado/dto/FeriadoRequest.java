package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FeriadoRequest {

    @NotNull
    @Size(max = 255)
    private String nome;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String dataFeriado;
    private Integer cidadeId;

    public static Feriado convertFrom(FeriadoRequest request) {
        Feriado feriado = new Feriado();
        BeanUtils.copyProperties(request, feriado);
        feriado.setDataFeriado(DateUtil.parseStringToLocalDate(request.getDataFeriado()));
        feriado.setFeriadoNacional(Objects.isNull(request.getCidadeId()) ? Eboolean.V : Eboolean.F);
        if (Objects.nonNull(request.getCidadeId())) {
            feriado.setCidade(new Cidade(request.getCidadeId()));
        }
        return feriado;
    }
}
