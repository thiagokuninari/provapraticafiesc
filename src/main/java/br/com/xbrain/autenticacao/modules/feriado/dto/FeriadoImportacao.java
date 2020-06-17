package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacao {

    private String nome;
    private LocalDate dataFeriado;
    private ETipoFeriado tipoFeriado;
    private Uf uf;
    private Cidade cidade;
    private List<String> motivoNaoImportacao;

    public boolean isTipoFeriadoComUfObrigatorio() {
        return ObjectUtils.nullSafeEquals(tipoFeriado, ETipoFeriado.ESTADUAL)
            || ObjectUtils.nullSafeEquals(tipoFeriado, ETipoFeriado.MUNICIPAL);
    }

    public boolean isTipoFeriadoComCidadeObrigatorio() {
        return ObjectUtils.nullSafeEquals(tipoFeriado, ETipoFeriado.MUNICIPAL);
    }

    public boolean isFeriadoNacional() {
        return ObjectUtils.nullSafeEquals(tipoFeriado, ETipoFeriado.NACIONAL);
    }
}
