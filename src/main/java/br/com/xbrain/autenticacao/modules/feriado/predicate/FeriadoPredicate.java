package br.com.xbrain.autenticacao.modules.feriado.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.QFeriado;
import com.querydsl.core.BooleanBuilder;

import java.time.LocalDate;

import static org.springframework.util.ObjectUtils.isEmpty;
import static br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.QImportacaoFeriado.importacaoFeriado;

public class FeriadoPredicate {

    private QFeriado feriado = QFeriado.feriado;
    private BooleanBuilder builder;

    public FeriadoPredicate() {
        this.builder = new BooleanBuilder();
    }

    public BooleanBuilder build() {
        return this.builder;
    }

    public FeriadoPredicate comNome(String nome) {
        if (!isEmpty(nome)) {
            builder.and(feriado.nome.containsIgnoreCase(nome));
        }
        return this;
    }

    public FeriadoPredicate comTipoFeriado(ETipoFeriado tipoFeriado) {
        if (!isEmpty(tipoFeriado)) {
            builder.and(feriado.tipoFeriado.eq(tipoFeriado));
        }
        return this;
    }

    public FeriadoPredicate comPeriodoDeDataFeriado(LocalDate dataInicio, LocalDate dataFim) {
        if (!isEmpty(dataInicio) && !isEmpty(dataFim)) {
            builder.and(feriado.dataFeriado.between(dataInicio, dataFim));
        }
        return this;
    }

    public FeriadoPredicate comCidadeOuEstado(Integer cidadeId, Integer estadoId) {
        return isEmpty(cidadeId)
            ? comEstado(estadoId)
            : comCidade(cidadeId, estadoId);
    }

    public FeriadoPredicate comCidade(Integer cidadeId, Integer estadoId) {
        if (!isEmpty(cidadeId) && !isEmpty(estadoId)) {
            builder.and(feriado.cidade.id.eq(cidadeId)
                .or(feriado.feriadoNacional.eq(Eboolean.V))
                .or(feriado.uf.id.eq(estadoId)
                    .and(feriado.tipoFeriado.eq(ETipoFeriado.ESTADUAL))));
        }
        return this;
    }

    public FeriadoPredicate comCidade(Integer cidadeId) {
        if (!isEmpty(cidadeId)) {
            builder.and(feriado.cidade.id.eq(cidadeId)
                .or(feriado.feriadoNacional.eq(Eboolean.V)));
        }
        return this;
    }

    public FeriadoPredicate comEstado(Integer estadoId) {
        if (!isEmpty(estadoId)) {
            builder.and(feriado.uf.id.eq(estadoId)
                .or(feriado.feriadoNacional.eq(Eboolean.V)));
        }
        return this;
    }

    public FeriadoPredicate comDataFeriado(LocalDate dataFeriado) {
        if (!isEmpty(dataFeriado)) {
            builder.and(feriado.dataFeriado.eq(dataFeriado));
        }
        return this;
    }

    public FeriadoPredicate excetoFeriadosFilhos() {
        builder.and(feriado.feriadoPai.isNull());
        return this;
    }

    public FeriadoPredicate comFeriadoPaiId(Integer feriadoPaiId) {
        if (!isEmpty(feriadoPaiId)) {
            builder.and(feriado.feriadoPai.id.eq(feriadoPaiId));
        }
        return this;
    }

    public FeriadoPredicate excetoExcluidos() {
        builder.and(feriado.situacao.ne(ESituacaoFeriado.EXCLUIDO));
        return this;
    }

    public FeriadoPredicate comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao situacaoFeriadoAutomacao) {
        if (situacaoFeriadoAutomacao != null) {
            builder.and(importacaoFeriado.situacaoFeriadoAutomacao.eq(situacaoFeriadoAutomacao));
        }
        return this;
    }
}
