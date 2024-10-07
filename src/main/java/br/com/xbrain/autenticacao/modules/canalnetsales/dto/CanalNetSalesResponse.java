package br.com.xbrain.autenticacao.modules.canalnetsales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanalNetSalesResponse {
    private Integer id;
    private String codigo;
}
