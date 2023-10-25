package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QSubCanal.subCanal;
import static net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils.isEmpty;
import static net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils.isNotBlank;

public class SubCanalPredicate {

    private final BooleanBuilder builder;

    public SubCanalPredicate() {
        this.builder = new BooleanBuilder();
    }

    public BooleanBuilder build() {
        return this.builder;
    }

    public SubCanalPredicate comCodigo(List<ETipoCanal> codigo) {
        if (!isEmpty(codigo)) {
            builder.and(subCanal.codigo.in(codigo));
        }
        return this;
    }

    public SubCanalPredicate comNome(String nome) {
        if (isNotBlank(nome)) {
            builder.and(subCanal.nome.equalsIgnoreCase(nome));
        }
        return this;
    }

    public SubCanalPredicate comSituacao(List<ESituacao> situacao) {
        if (!isEmpty(situacao)) {
            builder.and(subCanal.situacao.in(situacao));
        }
        return this;
    }

    public SubCanalPredicate comNovaChecagemCredito(Eboolean novaChecagemCredito) {
        if (!isEmpty(novaChecagemCredito)) {
            builder.and(subCanal.novaChecagemCredito.eq(novaChecagemCredito));
        }
        return this;
    }
}
