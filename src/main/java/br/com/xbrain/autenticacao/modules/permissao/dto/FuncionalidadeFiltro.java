package br.com.xbrain.autenticacao.modules.permissao.dto;

import lombok.Data;

@Data
public class FuncionalidadeFiltro {

    private Integer nivelId;
    private Integer departamentoId;
    private Integer cargoId;

    public FuncionalidadeFiltro() {
    }

    public FuncionalidadeFiltro(Integer departamentoId, Integer cargoId) {
        this.departamentoId = departamentoId;
        this.cargoId = cargoId;
    }
}
