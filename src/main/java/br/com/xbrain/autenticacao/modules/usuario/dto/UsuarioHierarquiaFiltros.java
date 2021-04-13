package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
