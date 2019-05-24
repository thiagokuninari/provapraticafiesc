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
}
