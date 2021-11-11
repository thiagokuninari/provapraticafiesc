package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

import static br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora.HORA;

@Data
@Builder
public class HorarioAcessoConsultaDto {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(HORA.getDescricao());

    private Integer id;
    private String siteNome;
    private String dataUltimaAlteracao;
    private String usuarioAlteracao;
    private List<HorarioAcessoDiaDto> diasAcesso;

    public static HorarioAcessoConsultaDto of(HorarioAcesso request) {
        return HorarioAcessoConsultaDto.builder()
            .id(request.getId())
            .siteNome(request.getSite().getNome())
            .dataUltimaAlteracao(request.getDataUltimaAlteracao().format(formatter))
            .usuarioAlteracao(request.getUsuarioAlteracao().getNome())
            .diasAcesso(request.getDias()
                .stream()
                .map(HorarioAcessoDiaDto::of)
                .collect(Collectors.toList()))
            .build();
    }
}
