package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgenteAutorizadoFiltros {

    private Integer cargoId;
    private Integer departamentoId;
    private Integer regionalId;
    private Integer ufId;
    private List<Integer> cidadesIds;
    private List<Integer> agentesAutorizadosIds;

    public static AgenteAutorizadoFiltros of(PublicoAlvoComunicadoFiltros filtro) {
        var response = new AgenteAutorizadoFiltros();
        BeanUtils.copyProperties(filtro, response);
        return response;
    }
}
