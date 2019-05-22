package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoDistribuicaoRequest {
    @NotNull(message = "É necessário informar o usuário de origem.")
    private Integer usuarioOrigemId;
    @NotNull(message = "É necessário informar o agente autorizado.")
    private Integer agenteAutorizadoId;
    @NotEmpty(message = "É necessário informar pelo menos um usuário de destino.")
    private List<AgendamentoUsuarioDto> agendamentosPorUsuario;
}
