package br.com.xbrain.autenticacao.modules.mailing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoAgenteAutorizadoResponse {
    private Integer agenteAutorizadoId;
    private Long quantidadeAgendamentos;
}
