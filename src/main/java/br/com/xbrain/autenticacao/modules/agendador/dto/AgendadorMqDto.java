package br.com.xbrain.autenticacao.modules.agendador.dto;

import br.com.xbrain.autenticacao.modules.agendador.enums.EStatusAgendador;
import lombok.Data;

@Data
public class AgendadorMqDto {

    private Integer historicoId;
    private String jobName;
    private String groupName;
    private String dataFimExecucao;
    private String erro;
    private EStatusAgendador status;
}
