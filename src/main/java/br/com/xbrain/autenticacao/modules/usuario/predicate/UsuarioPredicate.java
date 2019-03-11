package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.*;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;

public class UsuarioPredicate {

    private QUsuario usuario = QUsuario.usuario;
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
        if (!StringUtils.isEmpty(StringUtil.getOnlyNumbers(cpf))) {
            builder.and(usuario.cpf.eq(StringUtil.getOnlyNumbers(cpf)));
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

    public UsuarioPredicate ignorarAa() {
        builder.and(usuario.cargo.nivel.codigo.notIn(CodigoNivel.AGENTE_AUTORIZADO));
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
        if (cidadesIds.size() > 0) {
            builder.and(usuario.cidades.any().cidade.id.in(cidadesIds));
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

    private UsuarioPredicate daEmpresaEUnidadeDeNegocio(List<Empresa> empresaList,
                                                        List<UnidadeNegocio> unidadeNegocios) {
        builder.and(usuario.empresas.any().in(empresaList)
                .and(usuario.unidadesNegocios.any().in(unidadeNegocios)));
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

    private UsuarioPredicate daCidadeOuUsuarioCadastro(List<Integer> listaUsuario, Integer usuarioAutenticadoId) {
        builder.and(usuario.id.in(listaUsuario)
                .or(usuario.usuarioCadastro.id.eq(usuarioAutenticadoId)));
        return this;
    }

    public UsuarioPredicate filtraPermitidos(UsuarioAutenticado usuario, UsuarioService usuarioService) {
        if (!usuario.hasPermissao(AUT_VISUALIZAR_USUARIOS_AA)) {
            ignorarAa();
        }

        if (!usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            if (usuario.hasPermissao(AUT_VISUALIZAR_EMPRESA_UNIDADE)) {
                daEmpresaEUnidadeDeNegocio(
                        usuario.getUsuario().getEmpresas(),
                        usuario.getUsuario().getUnidadesNegocios()
                );

            } else if (usuario.hasPermissao(AUT_VISUALIZAR_CARTEIRA_HIERARQUIA)) {
                daCarteiraHierarquiaOuUsuarioCadastro(
                        usuarioService.getIdDosUsuariosSubordinados(usuario.getUsuario().getId(), false),
                        usuario.getUsuario().getId());

            } else if (usuario.hasPermissao(AUT_VISUALIZAR_CIDADE)) {
                daCidadeOuUsuarioCadastro(usuarioService.getIdDosUsuariosPorCidade(usuario.getUsuario().getId()),
                        usuario.getUsuario().getId());
            }
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
