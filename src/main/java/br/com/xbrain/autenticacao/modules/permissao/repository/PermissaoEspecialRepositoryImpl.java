package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static com.querydsl.jpa.JPAExpressions.select;

public class PermissaoEspecialRepositoryImpl extends CustomRepository<PermissaoEspecial>
        implements PermissaoEspecialRepositoryCustom {

    @Override
    public List<Funcionalidade> findPorUsuario(int usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(funcionalidade)
                .from(permissaoEspecial)
                .innerJoin(permissaoEspecial.funcionalidade, funcionalidade)
                .where(permissaoEspecial.usuario.id.eq(usuarioId)
                        .and(permissaoEspecial.dataBaixa.isNull()))
                .orderBy(permissaoEspecial.funcionalidade.aplicacao.nome.asc(),
                        permissaoEspecial.funcionalidade.nome.asc())
                .fetch();
    }

    @Override
    public void deletarPermissaoEspecialBy(List<Integer> funcionalidadeIds, List<Integer> usuarioIds) {
        new JPAQueryFactory(entityManager)
            .delete(permissaoEspecial)
            .where(permissaoEspecial.funcionalidade.id.in(funcionalidadeIds)
                .and(permissaoEspecial.usuario.id.in(usuarioIds)))
            .execute();
    }

    @Override
    public List<Integer> findPorUsuariosIdsEFuncionalidades(List<Integer> usuariosIds, List<Integer> funcionalidadeIds) {
        return new JPAQueryFactory(entityManager)
            .selectDistinct(permissaoEspecial.usuario.id)
            .from(permissaoEspecial)
            .where(permissaoEspecial.usuario.id.in(usuariosIds)
                .and(permissaoEspecial.usuario.id.notIn(
                    select(permissaoEspecial.usuario.id)
                    .from(permissaoEspecial)
                    .where(funcionalidade.id.in(funcionalidadeIds)))))
            .fetch();
    }
}
