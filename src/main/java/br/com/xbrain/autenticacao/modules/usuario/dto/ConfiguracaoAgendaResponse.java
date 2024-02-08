package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoAgendaResponse {
    private Integer id;
    private Integer qtdHorasAdicionais;
    private String descricao;
    private ESituacao situacao;
    private CodigoNivel nivel;
    private ECanal canal;
    private ETipoCanal subcanal;
    private String estruturaAa;

    public static ConfiguracaoAgendaResponse of(ConfiguracaoAgenda configuracaoAgenda) {
        var response = new ConfiguracaoAgendaResponse();
        BeanUtils.copyProperties(configuracaoAgenda, response);
        return response;
    }
}
