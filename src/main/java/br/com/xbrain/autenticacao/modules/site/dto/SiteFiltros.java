package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;

@Data
public class SiteFiltros {

    private String nome;
    private String cidade;
    private ESituacao situacao;

    public BooleanBuilder toPredicate() {
        return new SitePredicate()
                .comCidade(cidade)
                .comNome(nome)
                .comSituacao(situacao)
                .build();
    }
}
