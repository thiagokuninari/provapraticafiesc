package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipeVendasSupervisorResponse {
    private String equipeVendasNome;
    private String supervisorNome;

    public static EquipeVendasSupervisorResponse empty() {
        return new EquipeVendasSupervisorResponse();
    }
}
