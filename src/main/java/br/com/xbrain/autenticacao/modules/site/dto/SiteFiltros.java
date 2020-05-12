package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
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
    private Boolean naoPossuiDiscadora;
    private Integer discadoraId;

    public SitePredicate toPredicate() {
        return new SitePredicate()
            .comNome(nome)
            .comTimeZone(timeZone)
            .comSituacao(situacao)
            .naoPossuiDiscadora(naoPossuiDiscadora)
            .comDiscadoraId(discadoraId)
            .comCoordenadores(coordenadoresIds)
            .comSupervisores(supervisoresIds)
            .comEstados(estadosIds)
            .comCidades(cidadesIds);
    }
}
