package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CodigoFuncionalidade {

    AUT_VISUALIZAR_USUARIO("Visualizar Usuários"),
    AUT_VISUALIZAR_GERAL("AUT_VISUALIZAR_GERAL"),
    AUT_VISUALIZAR_USUARIOS_AA("Visualizar usuários dos Agentes Autorizados"),
    CTR_VISUALIZAR_CARTEIRA_HIERARQUIA("Visualizar Carteira/Hierarquia"),
    CTR_2033("Solicitar Ramal"),
    CTR_2034("Gerenciar Solicitacao Ramal"),
    AUT_2035("Distribuir Agendamentos"),
    AUT_2023("Gerenciar Cargos"),
    AUT_EMULAR_USUARIO("Emular Usuários"),
    AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO("Gerenciar Permissões por Cargo e Departamento"),
    AUT_GER_PERMISSAO_ESPECIAL_USUARIO("Gerenciar Permissões Especiais por Usuário"),
    CRN_ABRIR_CHAMADO("Abrir chamado CRN"),
    CRN_GERENCIAR_CHAMADO("Gerenciar chamado CRN"),
    CHM_ABRIR_CHAMADO("Abrir chamado"),
    CHM_TRATAR_CHAMADO_SUPORTE("Tratar chamado suporte"),
    CHM_TRATAR_CHAMADO_PROCESSO("Tratar chamado processo"),
    CHM_TRATAR_CHAMADO_GERAL("Tratar todos os chamados");

    @Getter
    private String descricao;

    public String getRole() {
        return "ROLE_" + name();
    }
}
