package br.com.xbrain.autenticacao.modules.feeder.dto;

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
public class AgenteAutorizadoPermissaoFeederDto {

    private Integer id;
    private Integer usuarioProprietarioId;
    private Integer usuarioCadastroId;
    private Eboolean permissaoFeeder;
    private List<Integer> colaboradoresVendasIds;

    public boolean hasPermissaoFeeder() {
        return Objects.equals(permissaoFeeder, Eboolean.V);
    }
}
