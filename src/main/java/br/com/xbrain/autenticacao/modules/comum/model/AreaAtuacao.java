package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;

public interface AreaAtuacao {

    String getNome();

    Integer getId();

    EAreaAtuacao getTipo();
}
