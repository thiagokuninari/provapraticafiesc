package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import java.util.List;

import lombok.Data;

@Data
public class HorarioAcessoRequest {
    private Integer id;
    private String site;
    private List<HorarioAcessoDiaDto> diasAcesso;
}
