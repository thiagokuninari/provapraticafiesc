package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EquipeVendaDto implements Serializable {

    private Integer id;
    private String descricao;
    private String canalVenda;
}
