package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgenteAutorizadoComunicadosFiltros {

    private Integer cargoId;
    private Integer departamentoId;
    private Integer regionalId;
    private Integer ufId;
    private List<Integer> cidadesIds;
    private List<Integer> agentesAutorizadosIds;
}
