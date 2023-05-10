package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoTecnicoIndicadorDto {

    private Integer agenteAutorizadoId;
    private List<Integer> usuariosIds;
    private Integer usuarioAutenticadoId;
}
