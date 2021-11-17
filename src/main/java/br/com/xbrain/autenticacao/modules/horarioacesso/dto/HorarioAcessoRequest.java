package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class HorarioAcessoRequest {
    private Integer id;
    @NotNull
    private Integer siteId;
    private List<DiaAcessoResponse> diasAcesso;
}
