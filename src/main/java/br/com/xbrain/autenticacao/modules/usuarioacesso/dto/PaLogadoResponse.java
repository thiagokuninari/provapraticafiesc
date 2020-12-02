package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaLogadoResponse {

    private Integer hora;
    private Integer totalUsuariosLogados;
}
