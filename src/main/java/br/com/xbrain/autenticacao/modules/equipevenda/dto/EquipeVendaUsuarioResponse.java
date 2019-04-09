package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipeVendaUsuarioResponse {

    private Integer id;
    private String usuarioNome;
    private String cargoNome;
    private Integer equipeVendaId;
    private Integer usuarioId;

}
