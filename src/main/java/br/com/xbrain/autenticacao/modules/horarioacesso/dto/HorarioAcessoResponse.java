package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAcessoResponse {

    private Integer horarioAcessoId;
    private Integer horarioHistoricoId;
    private String siteNome;
    private String dataAlteracao;
    private String usuarioAlteracaoNome;
    private List<HorarioAtuacaoDto> horariosAtuacao;

    public static HorarioAcessoResponse of(HorarioAcesso response) {
        return HorarioAcessoResponse.builder()
            .horarioAcessoId(response.getId())
            .siteNome(response.getSite().getNome())
            .dataAlteracao(dataFormatada(response.getDataAlteracao()))
            .usuarioAlteracaoNome(response.getUsuarioAlteracaoNome())
            .build();
    }

    public static HorarioAcessoResponse of(HorarioHistorico response) {
        return HorarioAcessoResponse.builder()
            .horarioHistoricoId(response.getId())
            .horarioAcessoId(response.getHorarioAcesso().getId())
            .siteNome(response.getHorarioAcesso().getSite().getNome())
            .dataAlteracao(dataFormatada(response.getDataAlteracao()))
            .usuarioAlteracaoNome(response.getUsuarioAlteracaoNome())
            .build();
    }

    public void setHorariosAtuacao(List<HorarioAtuacao> horariosAtuacao) {
        this.horariosAtuacao = horariosAtuacao.stream()
            .map(HorarioAtuacaoDto::of)
            .collect(Collectors.toList());
    }

    private static String dataFormatada(LocalDateTime data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
