package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaPredicate;
import lombok.Data;

@Data
public class ConfiguracaoAgendaFiltros {
    private Integer qtdHorasAdicionais;
    private String descricao;
    private CodigoNivel nivel;
    private ECanal canal;
    private ETipoCanal subcanal;
    private String estruturaAa;
    private ESituacao situacao;

    public ConfiguracaoAgendaPredicate toPredicate() {
        return new ConfiguracaoAgendaPredicate()
            .comCanal(canal)
            .comNivel(nivel)
            .comDescricao(descricao)
            .comQtdHorasAdicionais(qtdHorasAdicionais)
            .comSubCanal(subcanal)
            .comEstruturaAa(estruturaAa)
            .comSituacao(situacao);
    }
}
