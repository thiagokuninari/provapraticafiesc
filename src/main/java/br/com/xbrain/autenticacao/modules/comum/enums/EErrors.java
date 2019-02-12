package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.Getter;
import lombok.Setter;

public enum EErrors {

    ERRO_CONVERTER_EXCEPTION(
            "#001 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#001 - Erro ao tentar tratar uma exceção"),
    ERRO_OBTER_AA_BY_CNPJ(
            "#002 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#002 - Erro ao tentar recuperar o AA por CPNJ."),
    ERRO_OBTER_USUARIOS_AA_BY_ID(
            "#003 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#003 - Erro ao tentar recuperar os Usuários do AA por ID."),
    ERRO_VERIFICAR_PAUSA(
            "#004 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#004 - Erro ao verificar as pausas em andamento."),
    ERRO_OBTER_AA_BY_ID(
            "#005 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#005 - Erro ao tentar recuperar o AA por id."),
    ERRO_OBTER_EQUIPE_VENDAS_USUARIO(
            "#006 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#006 - Erro ao tentar recuperar as equipes de vendas do usuário."),
    ERRO_INATIVAR_SUPERVISOR_EQUIPE_VENDA(
            "#007 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#007 - Erro ao tentar inativar o usuário supervisor na equipe de vendas."),
    ERRO_INATIVAR_USUARIO_EQUIPE_VENDA(
            "#008 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#008 - Erro ao tentar inativar o usuário Assistente ou Vendedor na equipe de vendas."),
    ERRO_OBTER_SOCIO_PRINCIPAL_BY_AA_ID(
            "#009 - Desculpe, ocorreu um erro interno. Contate o administrador.",
                    "#009 - Erro ao tentar recuperar o sócio principal do agente autorizado."),
    ERRO_OBTER_DISCADORA_BY_ID(
            "#010 - Desculpe, ocorreu um erro interno. Contate o administrador.",
                    "#010 - Erro ao tentar recuperar a discadora pelo id."),
    ERRO_OBTER_LISTA_RAMAIS_BY_AA(
            "#011 - Desculpe, ocorreu um erro interno. Contate o administrador.",
                    "#011 - Erro ao tentar recuperar a lista de ramais do agente autorizado");

    @Getter
    @Setter
    private String descricao;
    @Getter
    private String descricaoTecnica;

    EErrors(String descricao, String descricaoTecnica) {
        this.descricao = descricao;
        this.descricaoTecnica = descricaoTecnica;
    }
}
