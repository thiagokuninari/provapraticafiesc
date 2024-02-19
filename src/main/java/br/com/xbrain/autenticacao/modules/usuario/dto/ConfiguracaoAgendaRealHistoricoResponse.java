package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaRealHistorico;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ConfiguracaoAgendaRealHistoricoResponse {

    private String acao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAcao;
    private Integer usuarioAcaoId;
    private String usuarioAcaoNome;

    public static ConfiguracaoAgendaRealHistoricoResponse of(ConfiguracaoAgendaRealHistorico historico) {
        return new ConfiguracaoAgendaRealHistoricoResponse(
            historico.getAcao().getDescricao(),
            historico.getDataAcao(),
            historico.getUsuarioAcaoId(),
            historico.getUsuarioAcaoNome()
        );
    }
}
