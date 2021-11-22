package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAcessoRequest {
    private Integer id;
    @NotNull
    private Integer siteId;
    @NotNull
    private List<HorarioAtuacaoDto> horariosAtuacao;
}
