package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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
    private ECanal canal;
    private String subcanal;
    private Integer subcanalId;
    private String estruturaAa;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;

    public static ConfiguracaoAgendaResponse of(ConfiguracaoAgendaReal configuracao) {
        var response = ConfiguracaoAgendaResponse.builder()
            .id(configuracao.getId())
            .dataCadastro(configuracao.getDataCadastro())
            .tipoConfiguracao(configuracao.getTipoConfiguracao())
            .qtdHorasAdicionais(configuracao.getQtdHorasAdicionais())
            .situacao(configuracao.getSituacao().getDescricao())
            .build();
        response.aplicarParametrosByTipoConfiguracao(configuracao);
        return response;
    }

    private void aplicarParametrosByTipoConfiguracao(ConfiguracaoAgendaReal configuracao) {
        if (tipoConfiguracao != ETipoConfiguracao.PADRAO) {
            tipoConfiguracao.getResponseConsumer().accept(this, configuracao);
        }
    }
}
