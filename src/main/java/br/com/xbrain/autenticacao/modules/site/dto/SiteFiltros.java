package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import com.querydsl.core.types.Predicate;
import lombok.Data;
import java.util.List;

@Data
public class SiteFiltros {

    private String nome;
    private ETimeZone timeZone;
    private ESituacao situacao;
    private List<Integer> coordenadoresIds;
    private List<Integer> supervisoresIds;
    private List<Integer> estadosIds;
    private List<Integer> cidadesIds;

    public Predicate toPredicate() {
        return new SitePredicate()
            .comNome(nome)
            .comTimeZone(timeZone)
            .comSituacao(situacao)
            .comCoordenadores(coordenadoresIds)
            .comSupervisores(supervisoresIds)
            .comEstados(estadosIds)
            .comCidades(cidadesIds)
            .build();
    }
}
