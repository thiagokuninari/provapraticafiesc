package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora.DATA_HORA_SEG;

@Data
@Builder
public class HorarioAcessoHistoricoResponse {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATA_HORA_SEG.getDescricao());

    private String dataAlteracao;
    private String usuarioNome;
    private List<DiaAcessoResponse> diasAcesso;

    public static HorarioAcessoHistoricoResponse of(HorarioAcessoHistorico request) {
        return HorarioAcessoHistoricoResponse.builder()
            .dataAlteracao(request.getDataUltimaAlteracao().format(formatter))
            .usuarioNome(request.getUsuarioAlteracao().getNome())
            .diasAcesso(request.getDiasAcesso()
                .stream()
                .map(DiaAcessoResponse::of)
                .collect(Collectors.toList()))
            .build();
    }
}
