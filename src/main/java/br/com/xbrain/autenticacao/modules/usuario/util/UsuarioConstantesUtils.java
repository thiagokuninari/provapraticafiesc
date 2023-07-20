package br.com.xbrain.autenticacao.modules.usuario.util;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.ValidacaoSubCanalException;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;

public class UsuarioConstantesUtils {

    public static final int POSICAO_ZERO = 0;
    public static final int POSICAO_UM = 1;
    public static final int MAX_CARACTERES_SENHA = 6;
    public static final ESituacao ATIVO = ESituacao.A;
    public static final ESituacao INATIVO = ESituacao.I;
    public static final String MSG_ERRO_AO_ATIVAR_USUARIO =
        "Erro ao ativar, o agente autorizado está inativo ou descredenciado.";
    public static final String MSG_ERRO_AO_REMOVER_CANAL_ATIVO_LOCAL =
        "Não é possível remover o canal Ativo Local, pois o usuário possui vínculo com o(s) Site(s): %s.";
    public static final String MSG_ERRO_AO_REMOVER_CANAL_AGENTE_AUTORIZADO =
        "Não é possível remover o canal Agente Autorizado, pois o usuário possui vínculo com o(s) AA(s): %s.";
    public static final String MSG_ERRO_AO_ALTERAR_CARGO_SITE =
        "Não é possível alterar o cargo, pois o usuário possui vínculo com o(s) Site(s): %s.";
    public static final String EX_USUARIO_POSSUI_OUTRA_EQUIPE =
        "Usuário já está cadastrado em outra equipe";
    public static final String OPERACAO = "Operação";
    public static final String AGENTE_AUTORIZADO = "Agente Autorizado";
    public static final String MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES =
        "Usuário inativo por excesso de consultas, não foi possível reativá-lo. Para reativação deste usuário é"
            + " necessário a abertura de um incidente no CA, anexando a liberação do diretor comercial.";
    public static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException(
        "Usuário não encontrado."
    );
    public static final ValidacaoException USUARIO_NAO_POSSUI_LOGIN_NET_SALES_EX = new ValidacaoException(
        "Usuário não possui login NetSales válido."
    );
    public static final ValidacaoException COLABORADOR_NAO_ATIVO = new ValidacaoException(
        "O colaborador não se encontra mais com a situação Ativo. Favor verificar seu cadastro."
    );
    public static ValidacaoException EMAIL_CADASTRADO_EXCEPTION = new ValidacaoException(
        "Email já cadastrado."
    );
    public static ValidacaoException EMAIL_ATUAL_INCORRETO_EXCEPTION =  new ValidacaoException(
        "Email atual está incorreto."
    );
    public static ValidacaoException SENHA_ATUAL_INCORRETA_EXCEPTION = new ValidacaoException(
        "Senha atual está incorreta."
    );
    public static ValidacaoException USUARIO_NOT_FOUND_EXCEPTION = new ValidacaoException(
        "O usuário não foi encontrado."
    );
    public static ValidacaoException USUARIO_ATIVO_LOCAL_POSSUI_AGENDAMENTOS_EX = new ValidacaoException(
        "Não foi possível inativar usuario Ativo Local com agendamentos"
    );
    public static ValidacaoException MSG_ERRO_USUARIO_NAO_POSSUI_SUBCANAIS = new ValidacaoException(
        "Usuário não possui sub-canais, deve ser cadastrado no mínimo um."
    );
    public static ValidacaoException MSG_ERRO_USUARIO_CARGO_SOMENTE_UM_SUBCANAL = new ValidacaoException(
        "Não é permitido cadastrar mais de um sub-canal para este cargo."
    );
    public static ValidacaoException MSG_ERRO_USUARIO_SEM_SUBCANAL_DA_HIERARQUIA = new ValidacaoException(
        "Usuário não possui sub-canal em comum com usuários da hierarquia."
    );
    public static ValidacaoSubCanalException MSG_ERRO_USUARIO_SEM_SUBCANAL_DOS_SUBORDINADOS = new ValidacaoSubCanalException(
        "Usuário não possui sub-canal em comum com usuários subordinados."
    );
    public static final List<CodigoCargo> cargosOperadoresBackoffice
        = List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO);
    public static List<CodigoCargo> CARGOS_PARA_INTEGRACAO_ATIVO_LOCAL = List.of(
        SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS);
    public static final List<CodigoCargo> LISTA_CARGOS_VALIDACAO_PROMOCAO = List.of(
        SUPERVISOR_OPERACAO, VENDEDOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_EXECUTIVO_VENDAS, COORDENADOR_OPERACAO);
    public static final List<CodigoCargo> LISTA_CARGOS_LIDERES_EQUIPE = List.of(
        SUPERVISOR_OPERACAO, COORDENADOR_OPERACAO);
    public static List<CodigoCargo> CARGOS_COM_MAIS_SUBCANAIS = List.of(
        COORDENADOR_OPERACAO, DIRETOR_OPERACAO, GERENTE_OPERACAO);
    public static List<CodigoCargo> CARGOS_PARA_INTEGRACAO_D2D = List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO,
        VENDEDOR_OPERACAO);
    public static List<CodigoCargo> LISTA_CARGOS_EQUIPE_VENDAS_D2D = List.of(COORDENADOR_OPERACAO, SUPERVISOR_OPERACAO,
        OPERACAO_CONSULTOR, OPERACAO_ANALISTA, ASSISTENTE_OPERACAO, VENDEDOR_OPERACAO, OPERACAO_EXECUTIVO_VENDAS);
    public static final List<CodigoCargo> CARGOS_OPERADORES_BACKOFFICE
        = List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO);
    public static final List<Integer> FUNCIONALIDADES_EQUIPE_TECNICA = List.of(16101);
    public static final int NUMERO_MAXIMO_TENTATIVAS_LOGIN_SENHA_INCORRETA = 3;
    public static final Integer PAP_ID = 1;
    public static final Integer PAP_PME_ID = 2;
    public static final Integer PAP_PREMIUM_ID = 3;
    public static final Integer INSIDE_SALES_PME_ID = 4;
}
