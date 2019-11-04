package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.QCluster;
import br.com.xbrain.autenticacao.modules.comum.model.QGrupo;
import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.comum.model.QSubCluster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;
import static java.util.Collections.singletonList;

@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioPredicate {

    private static final int QTD_MAX_IN_NO_ORACLE = 1000;

    private BooleanBuilder builder;

    public UsuarioPredicate() {
        this.builder = new BooleanBuilder();
    }

    public UsuarioPredicate comNome(String nome) {
        if (!StringUtils.isEmpty(nome)) {
            builder.and(usuario.nome.likeIgnoreCase("%" + nome + "%"));
        }
        return this;
    }

    public UsuarioPredicate comEmail(String email) {
        if (!StringUtils.isEmpty(email)) {
            builder.and(usuario.email.likeIgnoreCase("%" + email + "%"));
        }
        return this;
    }

    public UsuarioPredicate comSituacao(ESituacao situacao, boolean realocado) {
        if (Objects.nonNull(situacao) && !realocado) {
            builder.and(usuario.situacao.eq(situacao));
            builder.and(usuario.situacao.notIn(ESituacao.R));
        } else if (Objects.isNull(situacao) && realocado) {
            builder.and(usuario.situacao.eq(ESituacao.R));
        } else {
            builder.and(usuario.situacao.notIn(ESituacao.R));
        }
        return this;
    }

    public UsuarioPredicate comCpf(String cpf) {
        String numeroCpf = getOnlyNumbers(cpf);
        if (!StringUtils.isEmpty(numeroCpf)) {
            builder.and(usuario.cpf.eq(numeroCpf));
        }
        return this;
    }

    public UsuarioPredicate comEmpresas(List<Integer> empresasIds) {
        if (empresasIds.size() > 0) {
            builder.and(usuario.empresas.any().id.in(empresasIds));
        }
        return this;
    }

    public UsuarioPredicate comUnidadesNegocio(List<Integer> unidadesNegocioIds) {
        if (unidadesNegocioIds.size() > 0) {
            builder.and(usuario.unidadesNegocios.any().id.in(unidadesNegocioIds));
        }
        return this;
    }

    public UsuarioPredicate isAtivo(Eboolean ativo) {
        if (ativo == Eboolean.V) {
            builder.and(usuario.situacao.eq(ESituacao.A));
        }
        return this;
    }

    public UsuarioPredicate comNivel(List<Integer> nivelIds) {
        if (!CollectionUtils.isEmpty(nivelIds)) {
            builder.and(usuario.cargo.nivel.id.in(nivelIds));
        }
        return this;
    }

    public UsuarioPredicate ignorarAa(Boolean ignorar) {
        if (ignorar) {
            builder.and(usuario.cargo.nivel.codigo.notIn(CodigoNivel.AGENTE_AUTORIZADO));
        }
        return this;
    }

    public UsuarioPredicate ignorarXbrain(Boolean ignorar) {
        if (ignorar) {
            builder.and(usuario.cargo.nivel.codigo.notIn(CodigoNivel.XBRAIN));
        }
        return this;
    }

    public UsuarioPredicate comCargo(List<Integer> cargoIds) {
        if (!CollectionUtils.isEmpty(cargoIds)) {
            builder.and(usuario.cargo.id.in(cargoIds));
        }
        return this;
    }

    public UsuarioPredicate comCargos(Set<Integer> cargoIds) {
        if (!CollectionUtils.isEmpty(cargoIds)) {
            builder.and(usuario.cargo.id.in(cargoIds));
        }
        return this;
    }

    public UsuarioPredicate comDepartamento(List<Integer> departamentoIds) {
        if (!CollectionUtils.isEmpty(departamentoIds)) {
            builder.and(usuario.departamento.id.in(departamentoIds));
        }
        return this;
    }

    public UsuarioPredicate comUnidadeNegocio(Integer unidadeNegocioId) {
        if (!ObjectUtils.isEmpty(unidadeNegocioId)) {
            builder.and(usuario.unidadesNegocios.any().id.eq(unidadeNegocioId));
        }
        return this;
    }

    public UsuarioPredicate comCidade(List<Integer> cidadesIds) {
        if (!ObjectUtils.isEmpty(cidadesIds)) {
            builder.and(
                    ExpressionUtils.anyOf(
                            Lists.partition(cidadesIds, QTD_MAX_IN_NO_ORACLE)
                                    .stream()
                                    .map(ids -> usuario.cidades.any().cidade.id.in(ids))
                                    .collect(Collectors.toList())));
        }
        return this;
    }

    public UsuarioPredicate comIds(List<Integer> usuariosIds) {
        builder.and(usuario.id.in(usuariosIds));
        return this;
    }

    public UsuarioPredicate comId(Integer usuarioId) {
        if (usuarioId != null) {
            builder.and(usuario.id.eq(usuarioId));
        }
        return this;
    }

    public UsuarioPredicate comRegional(Integer regionalId) {
        if (regionalId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                    JPAExpressions.select(cidade.id)
                            .from(cidade)
                            .join(cidade.subCluster, QSubCluster.subCluster)
                            .join(QSubCluster.subCluster.cluster, QCluster.cluster)
                            .join(QCluster.cluster.grupo, QGrupo.grupo)
                            .join(QGrupo.grupo.regional, QRegional.regional)
                            .where(QRegional.regional.id.eq(regionalId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comGrupo(Integer grupoId) {
        if (grupoId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                    JPAExpressions.select(cidade.id)
                            .from(cidade)
                            .join(cidade.subCluster, QSubCluster.subCluster)
                            .join(QSubCluster.subCluster.cluster, QCluster.cluster)
                            .join(QCluster.cluster.grupo, QGrupo.grupo)
                            .where(QGrupo.grupo.id.eq(grupoId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comCluster(Integer clusterId) {
        if (clusterId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                    JPAExpressions.select(cidade.id)
                            .from(cidade)
                            .join(cidade.subCluster, QSubCluster.subCluster)
                            .join(QSubCluster.subCluster.cluster, QCluster.cluster)
                            .where(QCluster.cluster.id.eq(clusterId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comSubCluster(Integer subClusterId) {
        if (subClusterId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                    JPAExpressions.select(cidade.id)
                            .from(cidade)
                            .join(cidade.subCluster, QSubCluster.subCluster)
                            .where(QSubCluster.subCluster.id.eq(subClusterId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comCanal(ECanal canal) {
        if (!ObjectUtils.isEmpty(canal)) {
            builder.and(usuario.canais.any().eq(canal));
        }
        return this;
    }

    private UsuarioPredicate daCarteiraHierarquiaOuUsuarioCadastro(List<Integer> ids, int usuarioAutenticadoId) {
        builder.and(usuario.id.in(
                JPAExpressions
                        .select(usuario.id)
                        .from(usuario)
                        .leftJoin(usuario.usuariosHierarquia, usuarioHierarquia)
                        .where(usuarioHierarquia.usuario.id.in(ids)
                                .or(usuario.usuarioCadastro.id.eq(usuarioAutenticadoId)))));
        return this;
    }

    private UsuarioPredicate ignorarTodos() {
        builder.and(usuario.id.isNull());
        return this;
    }

    public UsuarioPredicate comCanalD2d(boolean todoCanalD2d) {
        if (todoCanalD2d) {
            builder.and(usuario.canais.contains(ECanal.D2D_PROPRIO));
        }
        return this;
    }

    public UsuarioPredicate comCanalAa(boolean todoCanalAa) {
        if (todoCanalAa) {
            builder.and(usuario.canais.contains(ECanal.AGENTE_AUTORIZADO));
        }
        return this;
    }

    public UsuarioPredicate comUsuariosId(List<Integer> usuariosId) {
        if (!ObjectUtils.isEmpty(usuariosId)) {
            builder.and(usuario.id.in(usuariosId));
        }
        return this;
    }

    public UsuarioPredicate comCargosId(List<Integer> cargosId) {
        if (!ObjectUtils.isEmpty(cargosId)) {
            builder.and(usuario.cargo.id.in(cargosId));
        }
        return this;
    }

    public UsuarioPredicate comCidadesId(List<Integer> cidadesId) {
        if (!ObjectUtils.isEmpty(cidadesId)) {
            builder.or(usuario.id.in(JPAExpressions
                    .select(usuarioCidade.usuario.id)
                    .from(usuarioCidade)
                    .where(usuarioCidade.cidade.id.in(cidadesId))));
        }
        return this;
    }

    public UsuarioPredicate comNiveisId(List<Integer> niveisId) {
        if (!ObjectUtils.isEmpty(niveisId)) {
            builder.and(usuario.cargo.nivel.id.in(niveisId));
        }
        return this;
    }

    public UsuarioPredicate comUltimaDataDeAcesso(LocalDate data) {
        if (!ObjectUtils.isEmpty(data)) {
            builder.and(usuario.dataUltimoAcesso.after(data.atStartOfDay()));
        }
        return this;
    }

    public UsuarioPredicate filtraPermitidos(UsuarioAutenticado usuario, UsuarioService usuarioService) {
        if (ObjectUtils.isEmpty(usuario)) {
            return this;
        }
        ignorarAa(!usuario.hasPermissao(AUT_VISUALIZAR_USUARIOS_AA));
        ignorarXbrain(!usuario.isXbrain());

        if (usuario.isUsuarioEquipeVendas()) {
            comIds(Stream.of(
                    usuarioService.getUsuariosPermitidosPelaEquipeDeVenda(),
                    usuarioService.getIdDosUsuariosSubordinados(usuario.getUsuario().getId(), true),
                    singletonList(usuario.getUsuario().getId()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));

        } else if (usuario.hasPermissao(CTR_VISUALIZAR_CARTEIRA_HIERARQUIA)) {
            daCarteiraHierarquiaOuUsuarioCadastro(
                    usuarioService.getIdDosUsuariosSubordinados(usuario.getUsuario().getId(), true),
                    usuario.getUsuario().getId());

        } else if (!usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            ignorarTodos();
        }
        return this;
    }

    public Predicate build() {
        return this.builder;
    }
}
