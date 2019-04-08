package br.com.xbrain.autenticacao.modules.comum.filtros;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.model.QEmpresa;
import com.querydsl.core.BooleanBuilder;

public class EmpresaPredicate {
    private QEmpresa empresa = QEmpresa.empresa;
    private BooleanBuilder builder;

    public EmpresaPredicate() {
        this.builder = new BooleanBuilder();
    }

    public EmpresaPredicate exibeXbrainSomenteParaXbrain(boolean isXbrain) {
        if (!isXbrain) {
            builder.and(empresa.codigo.ne(CodigoEmpresa.XBRAIN));
        }
        return this;
    }

    public EmpresaPredicate daUnidadeDeNegocio(Integer unidadeId) {
        if (unidadeId != null) {
            builder.and(empresa.unidadeNegocio.id.eq(unidadeId));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
