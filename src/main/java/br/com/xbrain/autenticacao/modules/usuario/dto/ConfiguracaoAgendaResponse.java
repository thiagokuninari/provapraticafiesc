package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoAgendaResponse {
    private Integer id;
    private Integer qtdHorasAdicionais;
    private ETipoConfiguracao tipoConfiguracao;
    private String situacao;
    private CodigoNivel nivel;
    private String canal;
    private String subcanal;
    private String estruturaAa;

    public static ConfiguracaoAgendaResponse of(ConfiguracaoAgendaReal configuracao) {
        var response = ConfiguracaoAgendaResponse.builder()
            .id(configuracao.getId())
            .tipoConfiguracao(configuracao.getTipoConfiguracao())
            .qtdHorasAdicionais(configuracao.getQtdHorasAdicionais())
            .situacao(configuracao.getSituacao().getDescricao())
            .build();
        response.aplicarParametrosByTipoConfiguracao(configuracao);
        return response;
    }

    private void aplicarParametrosByTipoConfiguracao(ConfiguracaoAgendaReal configuracao) {
        tipoConfiguracao.getResponseConsumer()
            .accept(this, configuracao);
    }
}
