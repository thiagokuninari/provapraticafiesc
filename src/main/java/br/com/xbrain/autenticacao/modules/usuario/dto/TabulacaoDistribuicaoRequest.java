package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TabulacaoDistribuicaoRequest {
    private final Integer agenteAutorizadoId;
    private final Integer usuarioOrigemId;
    private final List<AgendamentoUsuarioDto> colaboradores;
    private final Integer usuarioCadastroId;
    private final String usuarioCadastroNome;
}
