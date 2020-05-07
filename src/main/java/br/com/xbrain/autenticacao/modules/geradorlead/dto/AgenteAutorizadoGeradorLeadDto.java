package br.com.xbrain.autenticacao.modules.geradorlead.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAutorizadoGeradorLeadDto {

    private Integer id;
    private Integer usuarioProprietarioId;
    private Integer usuarioCadastroId;
    private Eboolean geradorLead;
    private List<Integer> colaboradoresVendasIds;

    public boolean isGeradorLead() {
        return Objects.equals(geradorLead, Eboolean.V);
    }
}
