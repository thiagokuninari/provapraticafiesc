package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static org.springframework.util.ObjectUtils.isEmpty;

@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioComunicadosPredicate {
    private static final int QTD_MAX_IN_NO_ORACLE = 1000;

    private BooleanBuilder builder;

    public UsuarioComunicadosPredicate() {
        this.builder = new BooleanBuilder();
    }

    public UsuarioComunicadosPredicate comFiltroCidadeParceiros(PublicoAlvoComunicadoFiltros filtros,
                                                                BooleanBuilder builder) {
        var predicate = comClusterId(filtros.getClusterId())
            .comCidadesIds(filtros.getCidadesIds())
            .comGrupoId(filtros.getGrupoId())
            .comUfId(filtros.getUfId())
            .comRegionalId(filtros.getRegionalId())
            .comSubClusterId(filtros.getSubClusterId()).build();

        builder.and(ExpressionUtils.anyOf(comUsuariosIds(filtros.getUsuariosFiltradosPorCidadePol()), predicate));

        return this;
    }

    private UsuarioComunicadosPredicate comSubClusterId(Integer subClusterId) {
        if (Objects.nonNull(subClusterId)) {
            builder.and(subCluster.id.eq(subClusterId));
        }
        return this;
    }

    private UsuarioComunicadosPredicate comUfId(Integer ufId) {
        if (Objects.nonNull(ufId)) {
            builder.and(uf1.id.eq(ufId));
        }
        return this;
    }

    private UsuarioComunicadosPredicate comRegionalId(Integer regionalId) {
        if (Objects.nonNull(regionalId)) {
            builder.and(regional.id.eq(regionalId));
        }
        return this;
    }

    private UsuarioComunicadosPredicate comGrupoId(Integer grupoId) {
        if (Objects.nonNull(grupoId)) {
            builder.and(grupo.id.eq(grupoId));
        }
        return this;
    }

    private UsuarioComunicadosPredicate comClusterId(Integer clusterId) {
        if (Objects.nonNull(clusterId)) {
            builder.and(cluster.id.eq(clusterId));
        }
        return this;
    }

    public UsuarioComunicadosPredicate comCidadesIds(List<Integer> cidadesIds) {
        if (!isEmpty(cidadesIds)) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(cidadesIds, QTD_MAX_IN_NO_ORACLE)
                        .stream()
                        .map(ids -> usuario.cidades.any().cidade.id.in(ids))
                        .collect(Collectors.toList())));
        }
        return this;
    }

    public Predicate comUsuariosIds(List<Integer> usuariosIds) {
        return ExpressionUtils.anyOf(
            Lists.partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                .stream()
                .map(usuario.id::in)
                .collect(Collectors.toList()));

    }

    public Predicate build() {
        return this.builder;
    }

}


