package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.xbrainutils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FeriadoRequest {

    private static ValidacaoException EX_ESTADO_OBRIGATORIO =
        new ValidacaoException("Para este Tipo de Feriado o campo ESTADO é obrigatório.");
    private static ValidacaoException EX_CIDADE_OBRIGATORIO =
        new ValidacaoException("Para este Tipo de Feriado o campo CIDADE é obrigatório.");
    private static ValidacaoException EX_ESTADO_NAO_PERMITIDO =
        new ValidacaoException("Para este Tipo de Feriado não é permitido cadastrar ESTADO.");
    private static ValidacaoException EX_CIDADE_NAO_PERMITIDO =
        new ValidacaoException("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");

    private Integer id;
    @NotEmpty
    @Size(max = 255)
    private String nome;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String dataFeriado;
    @NotNull
    private ETipoFeriado tipoFeriado;
    private Integer estadoId;
    private Integer cidadeId;

    public static Feriado convertFrom(FeriadoRequest request) {
        Feriado feriado = new Feriado();
        BeanUtils.copyProperties(request, feriado);
        feriado.setDataFeriado(DateUtils.parseStringToLocalDate(request.getDataFeriado()));
        feriado.setFeriadoNacional(Objects.isNull(request.getCidadeId()) ? Eboolean.V : Eboolean.F);
        if (Objects.nonNull(request.getCidadeId())) {
            feriado.setCidade(new Cidade(request.getCidadeId()));
        }
        return feriado;
    }

    public void validarDadosObrigatorios() {
        validarDadosDoFeriadoNacioanal();
        validarDadosDoFeriadoEstadual();
        validarDadosDoFeriadoMunicipal();
    }

    public boolean isFeriadoNacional() {
        return tipoFeriado != null && tipoFeriado == NACIONAL;
    }

    public boolean isTipoFeriado(ETipoFeriado tipoFeriado) {
        return this.tipoFeriado.equals(tipoFeriado);
    }

    private void validarDadosDoFeriadoNacioanal() {
        if (isFeriadoNacional()) {
            if (!isEmpty(estadoId)) {
                throw EX_ESTADO_NAO_PERMITIDO;
            }
            if (!isEmpty(cidadeId)) {
                throw EX_CIDADE_NAO_PERMITIDO;
            }
        }
    }

    private void validarDadosDoFeriadoEstadual() {
        if (isTipoFeriado(ESTADUAL)) {
            if (isEmpty(estadoId)) {
                throw EX_ESTADO_OBRIGATORIO;
            }
            if (!isEmpty(cidadeId)) {
                throw EX_CIDADE_NAO_PERMITIDO;
            }
        }
    }

    private void validarDadosDoFeriadoMunicipal() {
        if (isTipoFeriado(MUNICIPAL)) {
            if (isEmpty(estadoId)) {
                throw EX_ESTADO_OBRIGATORIO;
            }
            if (isEmpty(cidadeId)) {
                throw EX_CIDADE_OBRIGATORIO;
            }
        }
    }
}
