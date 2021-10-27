package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipeVendaSupervisorDto {

    private Integer id;
    private String descricao;
    private String canalVenda;
    private String supervisorNome;
}

