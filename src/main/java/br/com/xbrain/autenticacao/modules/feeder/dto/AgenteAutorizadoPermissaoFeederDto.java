package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
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

    private Integer agenteAutorizadoId;
    private Integer usuarioProprietarioId;
    private Integer usuarioCadastroId;
    private ETipoFeeder feeder;
    private List<Integer> colaboradoresVendasIds;
    boolean socioDeOutroAaComPermissaoFeeder;

    public boolean hasPermissaoFeeder() {
        return !Objects.equals(feeder, ETipoFeeder.NAO_FEEDER);
    }

    public boolean hasPermissaoFeederResidencial() {
        return Objects.equals(feeder, ETipoFeeder.RESIDENCIAL);
    }
}
