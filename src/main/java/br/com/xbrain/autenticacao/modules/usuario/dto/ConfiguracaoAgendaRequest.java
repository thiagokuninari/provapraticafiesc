package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.ValidationUtils;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.validationgroups.IConfiguracaoAgendaRealGroupsValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoAgendaRequest {
    @NotNull
    private Integer qtdHorasAdicionais;
    @NotNull
    private ETipoConfiguracao tipoConfiguracao;
    @NotNull(groups = IConfiguracaoAgendaRealGroupsValidation.Nivel.class)
    private CodigoNivel nivel;
    @NotNull(groups = IConfiguracaoAgendaRealGroupsValidation.Canal.class)
    private ECanal canal;
    @NotNull(groups = IConfiguracaoAgendaRealGroupsValidation.Canal.D2dProprio.class)
    private Integer subcanalId;
    @NotBlank(groups = IConfiguracaoAgendaRealGroupsValidation.Canal.AgenteAutorizado.class)
    private String estruturaAa;

    public void aplicarValidacoes() {
        ValidationUtils.aplicarValidacoes(this, tipoConfiguracao.getGroupValidator());
    }

    public void validarNivelOperacao() {
        if (nivel == CodigoNivel.OPERACAO) {
            throw new ValidacaoException("Não é possível criar configurações para esse nível, "
                + "por favor selecione um canal ou subcanal.");
        }
    }
}
