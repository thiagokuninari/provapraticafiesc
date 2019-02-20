package br.com.xbrain.autenticacao.modules.usuario.enums;

/**
 * Created by rafaelzeffa on 03/11/16.
 */
public enum CodigoFuncionalidade {
    AUT_VISUALIZAR_USUARIO(""),
    AUT_VISUALIZAR_GERAL(""),
    AUT_VISUALIZAR_EMPRESA_UNIDADE(""),
    AUT_VISUALIZAR_USUARIOS_AA(""),
    AUT_VISUALIZAR_USUARIOS_VAREJO(""),
    AUT_VISUALIZAR_CARTEIRA_HIERARQUIA(""),
    AUT_VISUALIZAR_CIDADE(""),
    AUT_VISUALIZAR_CARTEIRA(""),
    AUT_VISUALIZAR_VENDEDOR_PROPRIO(""),
    AUT_2033("AUT_SOLICITAR_RAMAIS_USUARIO"),
    AUT_2034("AUT_SOLICITAR_RAMAIS_GERENCIA"),
    POL_233(""),
    POL_GERENCIAR_AA(""),
    POL_VISUALIZAR_AA_EMPRESA_UNIDADE(""),
    POL_VISUALIZAR_AA_CARTEIRA(""),
    POL_VISUALIZAR_AA_CIDADE(""),
    POL_GERENCIAR_COMUNICADO_GERAL(""),
    POL_AGENTE_AUTORIZADO_APROVACAO_MSO(""),
    POL_AGENTE_AUTORIZADO_APROVACAO_MSO_NOVO_CADASTRO(""),
    POL_AGENTE_AUTORIZADO_APROVACAO_MSO_DESCREDENCIAMENTO_NAC(""),
    POL_PROJTOP_VISUALIZAR_METAS(""),
    POL_GERENCIAR_USUARIOS_EXECUTIVO(""),
    POL_EQUIPE_VENDA_EDITAR_LIDER("");

    private String descricao;

    CodigoFuncionalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return "ROLE_" + descricao;
    }
}
