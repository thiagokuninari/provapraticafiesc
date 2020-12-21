package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipeVendaDto implements Serializable {

    private Integer id;
    private String descricao;
    private String canalVenda;
}
