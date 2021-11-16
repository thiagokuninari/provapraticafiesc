package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HorarioAcessoConsultaDto {

    private Integer id;
    private String siteNome;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAlteracao;
    private String usuarioAlteracao;
    private List<DiaAcessoResponse> diasAcesso;

    public static HorarioAcessoConsultaDto of(HorarioAcesso request) {
        return HorarioAcessoConsultaDto.builder()
            .id(request.getId())
            .siteNome(request.getSite().getNome())
            .dataAlteracao(request.getDataAlteracao())
            .usuarioAlteracao(request.getUsuarioAlteracao().getNome())
            .build();
    }
}
