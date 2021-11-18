package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import lombok.*;

import java.util.List;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioAcessoRequest {
    private Integer id;
    @NotNull
    private Integer siteId;
    private List<DiaAcessoResponse> diasAcesso;
}
