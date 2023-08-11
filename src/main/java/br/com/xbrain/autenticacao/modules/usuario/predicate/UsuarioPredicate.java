package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
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

    private final BooleanBuilder builder;

    public UsuarioPredicate() {
        this.builder = new BooleanBuilder();
    }

    public UsuarioPredicate excluiIds(List<Integer> excluiIds) {
        if (!StringUtils.isEmpty(excluiIds)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(excluiIds, QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuario.id::notIn)
                    .collect(Collectors.toList())
            ));
        }
        return this;
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

    public UsuarioPredicate comUsuariosEmail(List<String> emails) {
        if (!isEmpty(emails)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(emails, QTD_MAX_IN_NO_ORACLE)
                    .parallelStream()
                    .map(usuario.email::in)
                    .collect(Collectors.toList()))
            );
        }

        return this;
    }

    public UsuarioPredicate comUsuariosCpfs(List<String> cpfs) {
        if (!isEmpty(cpfs)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(cpfs, QTD_MAX_IN_NO_ORACLE)
                    .parallelStream()
                    .map(usuario.cpf::in)
                    .collect(Collectors.toList()))
            );
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

    public UsuarioPredicate comOrganizacaoEmpresaId(Integer organizacaoEmpresaId) {
        if (nonNull(organizacaoEmpresaId)) {
            builder.and(usuario.organizacaoEmpresa.id.eq(organizacaoEmpresaId));
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

    public UsuarioPredicate comCargoCodigo(CodigoCargo cargo) {
        if (nonNull(cargo)) {
            builder.and(usuario.cargo.codigo.eq(cargo));
        }
        return this;
    }

    public UsuarioPredicate comCargo(List<Integer> cargoIds) {
        if (!CollectionUtils.isEmpty(cargoIds)) {
            builder.and(usuario.cargo.id.in(cargoIds));
        }
        return this;
    }

    public UsuarioPredicate comCargo(CodigoCargo cargo) {
        if (!isEmpty(cargo)) {
            builder.and(usuario.cargo.codigo.eq(cargo));
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

    public UsuarioPredicate comRegional(Integer regionalId, List<Integer> novasRegionaisIds) {
        if (regionalId != null) {
            if (novasRegionaisIds.contains(regionalId)) {
                comNovaRegional(regionalId);
            } else {
                builder.and(usuario.cidades.any().cidade.id.in(
                    JPAExpressions.select(cidade.id)
                        .from(cidade)
                        .join(cidade.subCluster, subCluster)
                        .join(subCluster.cluster, cluster)
                        .join(cluster.grupo, grupo)
                        .join(grupo.regional, regional)
                        .where(regional.id.eq(regionalId))
                ));
            }
        }
        return this;
    }

    private void comNovaRegional(Integer regionalId) {
        builder.and(usuario.cidades.any().cidade.id.in(
            JPAExpressions.select(cidade.id)
                .from(cidade)
                .join(cidade.uf, uf1)
                .join(cidade.regional, regional)
                .where(regional.id.eq(regionalId))
        ));
    }

    public UsuarioPredicate comUf(Integer ufId) {
        if (ufId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                JPAExpressions.select(cidade.id)
                    .from(cidade)
                    .join(cidade.uf, uf1)
                    .where(uf1.id.eq(ufId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comGrupo(Integer grupoId) {
        if (grupoId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                JPAExpressions.select(cidade.id)
                    .from(cidade)
                    .join(cidade.subCluster, subCluster)
                    .join(subCluster.cluster, cluster)
                    .join(cluster.grupo, grupo)
                    .where(grupo.id.eq(grupoId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comCluster(Integer clusterId) {
        if (clusterId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                JPAExpressions.select(cidade.id)
                    .from(cidade)
                    .join(cidade.subCluster, subCluster)
                    .join(subCluster.cluster, cluster)
                    .where(cluster.id.eq(clusterId))
            ));
        }
        return this;
    }

    public UsuarioPredicate comSubCluster(Integer subClusterId) {
        if (subClusterId != null) {
            builder.and(usuario.cidades.any().cidade.id.in(
                JPAExpressions.select(cidade.id)
                    .from(cidade)
                    .join(cidade.subCluster, subCluster)
                    .where(subCluster.id.eq(subClusterId))
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

    public UsuarioPredicate comCanais(Set<ECanal> canais) {
        if (!isEmpty(canais)) {
            builder.and(usuario.canais.any().in(canais));
        }
        return this;
    }

    public UsuarioPredicate daHierarquia(List<Integer> ids) {
        builder.and(usuario.usuariosHierarquia.any().usuarioSuperior.id.in(ids));
        return this;
    }

    private UsuarioPredicate somenteUsuariosBackoffice(UsuarioAutenticado usuario, UsuarioService usuarioService,
                                                       boolean incluirProrio) {

        var usuariosIds = usuarioService.buscarIdsUsuariosDeCargosInferiores(usuario.getNivelId());
        if (incluirProrio) {
            usuariosIds.add(usuario.getId());
        }
        comIds(usuariosIds);
        comOrganizacaoEmpresaId(usuario.getOrganizacaoId());

        return this;
    }

    public UsuarioPredicate ignorarTodos() {
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

    public UsuarioPredicate comCargosIds(List<Integer> cargosIds) {
        if (!isEmpty(cargosIds)) {
            builder.and(usuario.cargo.id.in(cargosIds));
        }
        return this;
    }

    public UsuarioPredicate comCidadesIds(List<Integer> cidadesIds, List<Integer> novasRegionaisIds,
                                          Integer clusterId, Integer grupoId, Integer regionalId,
                                          Integer subClusterId, Integer ufId) {
        if (!isEmpty(cidadesIds)) {
            comCidade(cidadesIds);
        } else if (!isEmpty(subClusterId)) {
            comSubCluster(subClusterId);
        } else if (!isEmpty(clusterId)) {
            comCluster(clusterId);
        } else if (!isEmpty(grupoId)) {
            comGrupo(grupoId);
        } else if (!isEmpty(ufId)) {
            comUf(ufId);
        } else if (!isEmpty(regionalId)) {
            comRegional(regionalId, novasRegionaisIds);
        }
        return this;
    }

    public UsuarioPredicate semUsuarioId(Integer usuarioId) {
        if (!isEmpty(usuarioId)) {
            builder.and(usuario.id.ne(usuarioId));
        }
        return this;
    }

    public UsuarioPredicate comNiveisIds(List<Integer> niveisIds) {
        if (!isEmpty(niveisIds)) {
            builder.and(usuario.cargo.nivel.id.in(niveisIds));
        }
        return this;
    }

    public UsuarioPredicate comUsuariosLogadosHoje(boolean comUsuariosLogadosHoje) {
        if (comUsuariosLogadosHoje) {
            builder.and(usuario.dataUltimoAcesso.after(LocalDate.now().atStartOfDay()));
        }
        return this;
    }

    public UsuarioPredicate filtraPermitidos(UsuarioAutenticado usuario, UsuarioService usuarioService,
                                             boolean incluirProprio) {
        if (isEmpty(usuario)) {
            return this;
        }
        ignorarFiltro(usuario);

        if (usuario.isUsuarioEquipeVendas()) {
            comIds(Stream.of(
                        usuarioService.getUsuariosPermitidosPelaEquipeDeVenda(),
                        usuarioService.getIdDosUsuariosSubordinados(usuario.getUsuario().getId(), incluirProprio),
                        singletonList(usuario.getUsuario().getId())
                    )
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );

        } else if (usuario.hasPermissao(CTR_VISUALIZAR_CARTEIRA_HIERARQUIA)
            || usuario.hasPermissao(AUT_20050)) {
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

    private void ignorarFiltro(UsuarioAutenticado usuario) {
        ignorarAa(!usuario.hasPermissao(AUT_VISUALIZAR_USUARIOS_AA));
        ignorarXbrain(!usuario.isXbrain());
    }

    public UsuarioPredicate comFiltroCidadeParceiros(UsuarioAutenticado usuario, UsuarioService usuarioService,
                                                     PublicoAlvoComunicadoFiltros filtros) {
        if (usuario.haveCanalAgenteAutorizado() && filtros.haveFiltrosDeLocalizacao()) {
            filtros.setUsuariosFiltradosPorCidadePol(usuarioService.getIdDosUsuariosParceiros(filtros));
            if (!filtros.getUsuariosFiltradosPorCidadePol().isEmpty()) {
                new UsuarioComunicadosPredicate()
                    .comFiltroCidadeParceiros(filtros, this.builder)
                    .build();
                return this;
            }
        }
        comEstruturaDeCidade(filtros);
        return this;
    }

    public UsuarioPredicate comEstruturaDeCidade(PublicoAlvoComunicadoFiltros filtros) {
        return comCluster(filtros.getClusterId())
            .comCidade(filtros.getCidadesIds())
            .comGrupo(filtros.getGrupoId())
            .comRegional(filtros.getRegionalId(), filtros.getNovasRegionaisIds())
            .comUf(filtros.getUfId())
            .comSubCluster(filtros.getSubClusterId());
    }

    public UsuarioPredicate filtraPermitidosComParceiros(UsuarioAutenticado usuario, UsuarioService usuarioService) {
        this.builder.and(new UsuarioPredicate()
            .filtraPermitidos(usuario, usuarioService, true)
            .ouComUsuariosIds(usuarioService.getIdDosUsuariosSubordinadosDoPol(usuario))
            .build());
        return this;
    }

    public UsuarioPredicate filtraPermitidosComParceiros(UsuarioAutenticado usuario, UsuarioService usuarioService,
                                                         PublicoAlvoComunicadoFiltros filtros) {
        this.builder.and(new UsuarioPredicate()
            .filtraPermitidos(usuario, usuarioService, false)
            .ouComUsuariosIds(usuarioService.getIdDosUsuariosSubordinadosDoPol(usuario, filtros))
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
                    CodigoCargo.ASSISTENTE_OPERACAO,
                    CodigoCargo.OPERACAO_EXECUTIVO_VENDAS
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

    public UsuarioPredicate comCodigosCargos(List<CodigoCargo> codigosCargos) {
        if (!isEmpty(codigosCargos)) {
            builder.and(usuario.cargo.codigo.in(codigosCargos));
        }

        return this;
    }

    public UsuarioPredicate comCodigosNiveis(List<CodigoNivel> codigosNiveis) {
        if (!isEmpty(codigosNiveis)) {
            builder.and(usuario.cargo.nivel.codigo.in(codigosNiveis));
        }

        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
