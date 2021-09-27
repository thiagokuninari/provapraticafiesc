package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipeVendaUsuarioFiltros {
    private Integer equipeVendaId;
    private List<Integer> equipeVendaIds;
    private Boolean ativo;
    private String codigoCargo;

    public Map toMap() {
        return new ObjectMapper().convertValue(this, Map.class);
    }
}

