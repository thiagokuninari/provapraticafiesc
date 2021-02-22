package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.QCluster;
import br.com.xbrain.autenticacao.modules.comum.model.QGrupo;
import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.comum.model.QSubCluster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioPredicate {

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

    public UsuarioPredicate comSituacoes(Collection<ESituacao> situacoes) {
        if (!isEmpty(situacoes)) {
            builder.and(usuario.situacao.in(situacoes));
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

    public UsuarioPredicate comOrganizacaoId(Integer organizacaoId) {
        if (nonNull(organizacaoId)) {
            builder.and(usuario.organizacao.id.eq(organizacaoId));
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

    public UsuarioPredicate comUnidadeNegocio(List<Integer> unidadeNegocioIds) {
        if (!isEmpty(unidadeNegocioIds)) {
            builder.and(usuario.unidadesNegocios.any().id.in(unidadeNegocioIds));
        }
        return this;
    }

    public UsuarioPredicate comCidade(List<Integer> cidadesIds) {
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

    public UsuarioPredicate comUsuariosIds(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                        .stream()
                        .map(usuario.id::in)
                        .collect(Collectors.toList())));
        }
        return this;
    }

    public UsuarioPredicate comIds(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(new ArrayList<>(usuariosIds), QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuario.id::in)
                    .collect(Collectors.toList()))
            );
        } else {
            ignorarTodos();
        }
        return this;
    }

    public UsuarioPredicate ouComUsuariosIds(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            builder.or(
                ExpressionUtils.anyOf(
                    Lists.partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                        .stream()
                        .map(usuario.id::in)
                        .collect(Collectors.toList())));
        }
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
        if (!isEmpty(canal)) {
            builder.and(usuario.canais.any().eq(canal));
        }
        return this;
    }

    public UsuarioPredicate comCanais(Collection<ECanal> canais) {
        if (!isEmpty(canais)) {
            builder.and(usuario.canais.any().in(canais));
        }
        return this;
    }

    private UsuarioPredicate somenteUsuariosBackoffice(UsuarioAutenticado usuario, UsuarioService usuarioService,
                                                       boolean incluirProrio) {

        var usuariosIds = usuarioService.buscarIdsUsuariosDeCargosInferiores(usuario.getNivelId());
        if (incluirProrio) {
            usuariosIds.add(usuario.getId());
        }
        comIds(usuariosIds);
        comOrganizacaoId(usuario.getOrganizacaoId());

        return this;
    }

    public UsuarioPredicate ignorarTodos() {
        builder.and(usuario.id.isNull());
        return this;
    }

    public UsuarioPredicate filtraPermitidos(UsuarioAutenticado usuario, UsuarioService usuarioService) {
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
            comIds(Stream.of(
                usuarioService.obterIdsPorUsuarioCadastroId(usuario.getUsuario().getId()),
                usuarioService.getIdDosUsuariosSubordinados(usuario.getUsuario().getId(), true))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        } else if (usuario.isBackoffice()) {
            somenteUsuariosBackoffice(usuario, usuarioService, true);
        } else if (!usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            ignorarTodos();
        }
        return this;
    }

    public UsuarioPredicate filtraPermitidosComParceiros(UsuarioAutenticado usuario, UsuarioService usuarioService) {
        this.builder.and(new UsuarioPredicate()
            .filtraPermitidos(usuario, usuarioService)
            .ouComUsuariosIds(usuarioService.getIdDosUsuariosSubordinadosDoPol(usuario))
            .build());
        return this;
    }

    public UsuarioPredicate filtrarPermitidosRelatorioLoginLogout(ECanal canal) {
        switch (canal) {
            case AGENTE_AUTORIZADO:
                builder.and(usuario.cargo.codigo.in(
                    CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D,
                    CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS,
                    CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D,
                    CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
                    CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
                    CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS
                ));
                break;
            case D2D_PROPRIO:
                builder.and(usuario.cargo.codigo.in(
                    CodigoCargo.VENDEDOR_OPERACAO,
                    CodigoCargo.ASSISTENTE_OPERACAO
                ));
                break;
            default:
        }
        return this;
    }

    public UsuarioPredicate comCodigoCargo(CodigoCargo codigoCargo) {
        Optional.ofNullable(codigoCargo)
            .map(usuario.cargo.codigo::eq)
            .map(builder::and);

        return this;
    }

    public UsuarioPredicate comCodigosNiveis(List<CodigoNivel> codigosNiveis) {
        if (!isEmpty(codigosNiveis)) {
            builder.and(usuario.cargo.nivel.codigo.in(codigosNiveis));
        }

        return this;
    }

    public UsuarioPredicate comCodigosCargos(List<CodigoCargo> codigosCargos) {
        if (!isEmpty(codigosCargos)) {
            builder.and(usuario.cargo.codigo.in(codigosCargos));
        }

        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
