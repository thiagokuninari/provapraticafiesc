package br.com.xbrain.autenticacao.modules.comum.helper;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;

public class OrganizacaoHelper {

    public static Organizacao umaOrganizacao(Integer id, String codigo) {
        return Organizacao.builder()
                .id(id)
                .codigo(codigo)
                .nome(codigo)
                .build();
    }

    public static Organizacao umaOrganizacaoBcc() {
        return umaOrganizacao(1, "BCC");
    }

    public static Organizacao umaOrganizacaoCallink() {
        return umaOrganizacao(2, "CALLINK");
    }

    public static Organizacao umaOrganizacaoProprio() {
        return umaOrganizacao(3, "PROPRIO");
    }

    public static Organizacao umaOrganizacaoAtento() {
        return umaOrganizacao(4, "ATENTO");
    }

    public static Organizacao umaOrganizacaoaVgx() {
        return umaOrganizacao(5, "VGX");
    }
}
