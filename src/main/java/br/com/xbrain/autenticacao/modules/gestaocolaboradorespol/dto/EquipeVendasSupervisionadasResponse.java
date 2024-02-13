package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipeVendasSupervisionadasResponse {

    public Integer id;
    public String descricao;
    public String canalVenda;

}
