package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            .usuarioAlteracao(request.getUsuarioAlteracaoNome())
            .build();
    }

    public static HorarioAcessoConsultaDto of(HorarioAcessoHistorico request) {
        return HorarioAcessoConsultaDto.builder()
            .id(request.getId())
            .dataAlteracao(request.getDataAlteracao())
            .usuarioAlteracao(request.getUsuarioAlteracaoNome())
            .build();
    }

    public void setDiasAcesso(List<DiaAcesso> request) {
        this.diasAcesso = request.stream()
            .map(DiaAcessoResponse::of)
            .collect(Collectors.toList());
    }

    public void setDiasAcessoHist(List<DiaAcessoHistorico> request) {
        this.diasAcesso = request.stream()
            .map(DiaAcessoResponse::of)
            .collect(Collectors.toList());
    }
}
