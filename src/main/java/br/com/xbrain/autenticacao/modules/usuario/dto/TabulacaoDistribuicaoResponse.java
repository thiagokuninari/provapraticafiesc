package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TabulacaoDistribuicaoResponse {
    private final Integer usuarioId;
    private final List<Integer> tabulacoesHpsIds;
}
