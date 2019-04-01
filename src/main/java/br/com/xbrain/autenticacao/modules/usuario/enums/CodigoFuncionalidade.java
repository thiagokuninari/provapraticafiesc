package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CodigoFuncionalidade {

    AUT_VISUALIZAR_USUARIO("Visualizar Usuários"),
    AUT_VISUALIZAR_GERAL("AUT_VISUALIZAR_GERAL"),
    AUT_VISUALIZAR_EMPRESA_UNIDADE("Visualizar Empresa/Unidade de Negócio"),
    AUT_VISUALIZAR_USUARIOS_AA("Visualizar usuários dos Agentes Autorizados"),
    AUT_VISUALIZAR_USUARIOS_VAREJO("Visualizar usuários do Varejo"),
    AUT_VISUALIZAR_CARTEIRA_HIERARQUIA("Visualizar Carteira/Hierarquia"),
    AUT_VISUALIZAR_CIDADE("Visualizar Usuários por Cidades"),
    AUT_2033("Solicitar Ramal"),
    AUT_2034("Gerenciar Solicitacao Ramal"),
    AUT_2023("Gerenciar Cargos"),
    AUT_EMULAR_USUARIO("Emular Usuários"),
    AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO("Gerenciar Permissões por Cargo e Departamento"),
    AUT_GER_PERMISSAO_ESPECIAL_USUARIO("Gerenciar Permissões Especiais por Usuário"),
    POL_GERENCIAR_USUARIOS_EXECUTIVO("Gerenciar Usuários Executivos");

    @Getter
    private String descricao;
}
