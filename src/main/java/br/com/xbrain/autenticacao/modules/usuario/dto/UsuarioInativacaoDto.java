package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInativacaoDto {

    @NotNull
    private Integer idUsuario;

    private Integer idUsuarioInativacao;

    private CodigoMotivoInativacao codigoMotivoInativacao;

    @Size(max = 250)
    private String observacao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    @JsonIgnore
    public boolean isFerias() {
        return codigoMotivoInativacao == CodigoMotivoInativacao.FERIAS
            && !isEmpty(dataInicio)
            && !isEmpty(dataFim);
    }

    @JsonIgnore
    public boolean isAfastamento() {
        return codigoMotivoInativacao == CodigoMotivoInativacao.AFASTAMENTO
            && !isEmpty(dataInicio);
    }
}
