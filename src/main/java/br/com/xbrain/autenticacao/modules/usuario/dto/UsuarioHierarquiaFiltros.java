package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioHierarquiaFiltros {

    private Integer siteId;
    private Integer coordenadorId;
    @Builder.Default
    private Boolean buscarInativo = true;
    private Integer equipeVendaId;

    public Boolean apenasSiteId() {
        return Objects.nonNull(siteId) && Objects.isNull(coordenadorId) && Objects.isNull(equipeVendaId);
    }
}
