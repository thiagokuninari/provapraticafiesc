package br.com.xbrain.autenticacao.modules.geradorlead.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAutorizadoGeradorLeadDto {

    private Integer id;
    private Integer usuarioProprietarioId;
    private Eboolean geradorLead;
    private List<Integer> colaboradoresVendasIds;

}
