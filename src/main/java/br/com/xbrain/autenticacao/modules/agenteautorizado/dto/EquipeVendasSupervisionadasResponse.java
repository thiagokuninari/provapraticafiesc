package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EquipeVendasSupervisionadasResponse implements Serializable {

    public Integer id;
    public String descricao;
    public String canalVenda;

}
