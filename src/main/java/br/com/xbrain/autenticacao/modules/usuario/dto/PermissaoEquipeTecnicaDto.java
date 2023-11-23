package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissaoEquipeTecnicaDto {

    private Integer agenteAutorizadoId;
    private Integer usuarioProprietarioId;
    private List<Integer> sociosSecundariosIds;
    private Integer usuarioCadastroId;
    private boolean hasEquipeTecnica;
    private boolean socioDeOutroAaComPermissaoEquipeTecnica;

    public boolean hasEquipeTecnica() {
        return this.hasEquipeTecnica;
    }
}
