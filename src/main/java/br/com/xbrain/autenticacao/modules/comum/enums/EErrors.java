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
    ERRO_OBTER_SOCIO_PRINCIPAL_BY_AA_ID(
            "#007 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#007 - Erro ao tentar recuperar o sócio principal do agente autorizado."),
    ERRO_OBTER_DISCADORA_BY_ID(
            "#008 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#008 - Erro ao tentar recuperar a discadora pelo id."),
    ERRO_OBTER_LISTA_RAMAIS_BY_AA(
            "#009 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#009 - Erro ao tentar recuperar a lista de ramais do agente autorizado"),
    ERRO_INATIVAR_SUPERVISOR_EQUIPE_VENDA(
            "#010 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#010 - Erro ao tentar inativar o usuário supervisor na equipe de vendas."),
    ERRO_INATIVAR_USUARIO_EQUIPE_VENDA(
            "#011 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#011 - Erro ao tentar inativar o usuário Assistente ou Vendedor na equipe de vendas."),
    ERRO_OBTER_COLABORADORES_DO_AA(
            "#012 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#012 - Erro ao obter os colaboradores do agente autorizado."),
    ERRO_INATIVAR_COLABORADOR_VENDAS(
            "#013 - Desculpe, ocorreu um erro interno. Contate o administrador.",
            "#013 - Erro ao inativar o colaborador de vendas."),
    ERRO_OBTER_COLABORADOR_VENDAS_BY_ID(
            "#014 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#014 - Erro ao tentar recuperar o colaborador de vendas por ID do usuário."),
    ERRO_DISTRIBUIR_TABULACOES(
        "#015 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#015 - Erro ao tentar distribuir os agendamentos do usuário."),
    ERRO_RECUPERAR_TABULACOES(
        "#016 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#016 - Erro ao tentar recuperar os agendamentos do usuário."),
    ERRO_OBTER_EQUIPE_VENDAS_USUARIOS_PERMITIDOS(
        "#017 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#017 - Erro ao tentar recuperar usuários permitidos por equipe."),
    ERRO_OBTER_CIDADE_DO_POL(
        "#017 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#017 - Erro ao tentar recuperar cidades do pol."),
    ERRO_OBTER_AA(
        "#018 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#018 - Erro ao tentar recuperar o AA."),
    ERRO_DESVINCULAR_RAMAIS(
        "#019 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#019 - Erro ao desvincular ramais."),
    ERRO_LIMPAR_CACHE_ATIVO(
        "#020 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#020 - Erro ao limpar cache ativo"),
    ERRO_OBTER_ESTRUTURA_AA(
        "#019 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#019 - Erro ao tentar recuperar a Estrutura do AA."
    ),
    ERRO_OBTER_CEP(
        "#020 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#020 - Erro ao tentar buscar o cep"
    ),
    ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_HOJE(
        "#021 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#021 - Erro ao tentar recuperar o relatório de logins e logouts de hoje."
    ),
    ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_CSV(
        "#022 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#022 - Erro ao tentar recuperar o relatório de logins e logouts CSV."
    ),
    ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_USUARIOS_IDS(
        "#023 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#023 - Erro ao tentar recuperar os ids dos usuários dos acessos."
    ),
    ERRO_OBTER_IDS_USUARIOS_SUBORDINADOS(
        "#024 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#024 - Erro ao tentar recuperar os ids dos usuários subordinados."
    ),
    ERRO_BUSCAR_USUARIOS_DO_AA(
        "#030 - Desculpe, ocorreu um erro interno. Contate a administrador.",
        "#030 - Ocorreu um erro ao buscar usuários do agente autorizado"
    ),
    ERRO_OBTER_USUARIOS_LOGADOS_POR_HORA(
        "#032 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#032 - Erro ao tentar recuperar o número de usuários logados por hora."),
    ERRO_BUSCAR_TODOS_USUARIOS_DOS_AAS(
        "#033 - Desculpe, ocorreu um erro interno. Contate a administrador.",
        "#033 - Ocorreu um erro ao buscar todos os usuários dos agentes autorizados."),
    ERRO_LIMPAR_CACHE_CALL(
        "#034 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#034 - Erro ao limpar cache dos feriados na telefonia."),
    ERRO_OBTER_AA_USUARIO_DTO_BY_USUARIO_ID(
        "#035 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#035 - Ocorreu um Erro ao buscar agente autorizado usurario dto."),
    ERRO_CONSULTAR_STATUS_RAMAL_USUARIO(
        "#21 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#21 - Erro ao tentar consultar status do ramal pelo usuário autenticado."),
    ERRO_CONSULTAR_STATUS_TABULACAO_USUARIO(
        "#022 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#022 - Erro ao tentar consultar status da tabulação do usuário."),
    ERRO_LIBERAR_RAMAL_USUARIO(
        "#023 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#023 - Erro ao tentar liberar ramal do usuário autenticado."),
    ERRO_OBTER_RELATORIO_LOGINS_LOGOUTS_ENTRE_DATAS(
        "#036 - Desculpe, ocorreu um erro interno. Contate o administrador.",
        "#036 - Erro ao tentar recuperar o relatório de logins e logouts entre datas.");

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
