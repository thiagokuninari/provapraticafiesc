package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CodigoFuncionalidade {

    APPLICATION("Visualizar via integração"),
    AUT_VISUALIZAR_USUARIO("Visualizar Usuários"),
    AUT_VISUALIZAR_GERAL("AUT_VISUALIZAR_GERAL"),
    AUT_VISUALIZAR_USUARIOS_AA("Visualizar usuários dos Agentes Autorizados"),
    CTR_VISUALIZAR_CARTEIRA_HIERARQUIA("Visualizar Carteira/Hierarquia"),
    CTR_2033("Solicitar Ramal"),
    CTR_2034("Gerenciar Solicitacao Ramal"),
    AUT_2023("Gerenciar Cargos"),
    AUT_EMULAR_USUARIO("Emular Usuários"),
    AUT_DESLOGAR_USUARIO("Deslogar Usuários"),
    AUT_2046("Visualizar Sites"),
    AUT_2047("Gerenciar Sites"),
    AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO("Gerenciar Permissões por Cargo e Departamento"),
    AUT_GER_PERMISSAO_ESPECIAL_USUARIO("Gerenciar Permissões Especiais por Usuário"),
    AUT_2100("Visualizar relatório de login / logout"),
    AUT_INATIVAR_USUARIOS_SEM_ACESSO("Inativar Usuários Sem Acesso"),
    CRN_ABRIR_CHAMADO("Abrir chamado CRN"),
    CRN_GERENCIAR_CHAMADO("Gerenciar chamado CRN"),
    CHM_ABRIR_CHAMADO("Abrir chamado"),
    CHM_ADM_CHAMADOS("Administrador do suporte"),
    CHM_TRATAR_CHAMADO_PROCESSO("Tratar chamado processo"),
    CHM_TRATAR_CHAMADO_GERAL("Tratar todos os chamados"),
    MLG_5013("Distribuir Agendamentos Proprietários"),
    CTR_2050("Gerenciar Feriados"),
    VDS_3059("Visualizar Relatório Consulta de Endereço"),
    REL_10012("Relatório Consulta de Endereço"),
    VDS_3061("Visualizar Relatório Consulta de Crédito"),
    REL_10014("Relatório Consulta de Crédito"),
    FDR_GERENCIAR_LEAD("Gerenciar Lead"),
    MLG_5018("Gerenciar Distribuição Mailing Segmentação"),
    CTR_2044("Visualizar todos os Sites"),
    INT_7007("Resgatar tratativas para motiva"),
    AUT_20009("Gerenciar Horários de Acesso"),
    AUT_20010("Adicionar novo usuário"),
    CTR_20014("Solicitar Ramal AA"),
    CTR_20015("Solicitar Ramal D2D"),
    AUT_20024("Visualizar status do Horário de Acesso"),
    AUT_20025("Editar nova checagem de crédito - Sub-canais"),
    BKO_PRIORIZAR_INDICACOES("Priorizar Indicações Técnico"),
    VAR_GERENCIAR_ORGANIZACOES("Gerenciar Organizações"),
    AUT_21615("Gerenciar Configurações de Agenda");

    @Getter
    private String descricao;

    public String getRole() {
        return "ROLE_" + name();
    }
}
