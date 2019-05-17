package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAgenteAutorizadoAgendamentoResponse {
    private Integer id;
    private String nome;
    private String equipeVendasNome;
    private String supervisorNome;
}
