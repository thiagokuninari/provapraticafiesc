package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;

    public static ConfiguracaoAgendaResponse of(ConfiguracaoAgendaReal configuracao) {
        var response = new ConfiguracaoAgendaResponse();
        BeanUtils.copyProperties(configuracao, response);
        response.setSituacao(configuracao.getSituacao().getDescricao());
        response.setSubcanal(Optional.ofNullable(configuracao.getSubcanalId())
                .map(ETipoCanal::valueOf)
                .map(ETipoCanal::getDescricao)
                .orElse(null));
        return response;
    }
}
