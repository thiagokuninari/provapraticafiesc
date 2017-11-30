package br.com.xbrain.autenticacao.infra;

import com.querydsl.core.JoinType;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;

public class JoinDescriptor {

    public final EntityPath path;
    public final CollectionExpression collectionExpression;
    public final Path alias;
    public final JoinType type;

    private JoinDescriptor(EntityPath path, Path alias, JoinType type) {
        this.path = path;
        this.alias = alias;
        this.type = type;
        this.collectionExpression = null;
    }

    private JoinDescriptor(CollectionExpression collectionExpression, Path alias, JoinType type) {
        this.path = null;
        this.alias = alias;
        this.type = type;
        this.collectionExpression = collectionExpression;
    }

    public static JoinDescriptor innerJoin(EntityPath path, Path alias) {
        return new JoinDescriptor(path, alias, JoinType.INNERJOIN);
    }

    public static JoinDescriptor innerJoin(EntityPath path) {
        return new JoinDescriptor(path, null, JoinType.INNERJOIN);
    }

    public static JoinDescriptor innerJoin(CollectionExpression collectionExpression, Path alias) {
        return new JoinDescriptor(collectionExpression, alias, JoinType.INNERJOIN);
    }

    public static JoinDescriptor innerJoin(CollectionExpression collectionExpression) {
        return new JoinDescriptor(collectionExpression, null, JoinType.INNERJOIN);
    }

    public static JoinDescriptor leftJoin(EntityPath path, Path alias) {
        return new JoinDescriptor(path, alias, JoinType.LEFTJOIN);
    }

    public static JoinDescriptor leftJoin(EntityPath path) {
        return new JoinDescriptor(path, null, JoinType.LEFTJOIN);
    }

    public static JoinDescriptor leftJoin(CollectionExpression collectionExpression, Path alias) {
        return new JoinDescriptor(collectionExpression, alias, JoinType.LEFTJOIN);
    }

    public static JoinDescriptor leftJoin(CollectionExpression collectionExpression) {
        return new JoinDescriptor(collectionExpression, null, JoinType.LEFTJOIN);
    }

}