package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class AgendamentoDistribuicaoListagemResponse {
    private final Integer agenteAutorizadoId;
    private final String cnpjRazaoSocial;
    private final String equipeVendasNome;
    private final String supervisorNome;
    private final Long quantidadeAgendamentos;
    private final Long quantidadeAgendamentosRestantes;
    private final List<AgendamentoUsuarioDto> agendamentosPorUsuarios;
}
