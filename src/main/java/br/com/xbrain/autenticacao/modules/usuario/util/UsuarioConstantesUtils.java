package br.com.xbrain.autenticacao.modules.usuario.util;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;

public class UsuarioConstantesUtils {

    public static final int POSICAO_UM = 1;

    public static final ESituacao ATIVO = ESituacao.A;

    public static final ESituacao INATIVO = ESituacao.I;

    public static final String OPERACAO = "Operação";

    public static final String AGENTE_AUTORIZADO = "Agente Autorizado";

    public static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException(
        "Usuário não encontrado."
    );

    public static final List<CodigoCargo> CARGOS_COM_MAIS_SUBCANAIS = List.of(
        COORDENADOR_OPERACAO, DIRETOR_OPERACAO, GERENTE_OPERACAO);

    public static final List<CodigoCargo> LISTA_CARGOS_EQUIPE_VENDAS_D2D = List.of(COORDENADOR_OPERACAO, SUPERVISOR_OPERACAO,
        OPERACAO_CONSULTOR, OPERACAO_ANALISTA, ASSISTENTE_OPERACAO, VENDEDOR_OPERACAO, OPERACAO_EXECUTIVO_VENDAS);

}
