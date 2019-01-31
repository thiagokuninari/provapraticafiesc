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
            "#005 - Erro ao tentar recuperar o AA por id.");

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
