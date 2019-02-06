package br.com.xbrain.autenticacao.config.database;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StringType;

public class CustomOracle10gDialect extends Oracle10gDialect {

    public CustomOracle10gDialect() {
        super();
        registerFunction("wm_concat", new StandardSQLFunction("wm_concat", new StringType()));
    }
}
