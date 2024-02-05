package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class ConfiguracaoAgendaRequest {
    @NotNull
    private Integer qtdHorasAdicionais;
    @NotBlank
    private String descricao;
    private CodigoNivel nivel;
    private ECanal canal;
    private ETipoCanal subcanal;
    private String estruturaAa;
}
