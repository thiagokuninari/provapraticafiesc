package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.model.QMotivoInativacao.motivoInativacao;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHistorico.usuarioHistorico;

public class UsuarioHistoricoRepositoryImpl
    extends CustomRepository<UsuarioHistorico> implements UsuarioHistoricoRepositoryCustom {

    @Override
    public Optional<UsuarioHistorico> getUltimoHistoricoPorUsuario(Integer usuarioId) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(usuarioHistorico)
                .from(usuarioHistorico)
                .where(usuarioHistorico.usuario.id.eq(usuarioId))
                .orderBy(usuarioHistorico.dataCadastro.desc())
                .fetchFirst());
    }

    @Override
    public List<UsuarioHistorico> getHistoricoDoUsuario(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuarioHistorico)
            .leftJoin(usuarioHistorico.motivoInativacao).fetchJoin()
            .leftJoin(usuarioHistorico.usuarioAlteracao).fetchJoin()
            .where(usuarioHistorico.usuario.id.eq(usuarioId))
            .orderBy(usuarioHistorico.dataCadastro.desc())
            .fetch();
    }

    public List<UsuarioHistorico> findAllCompleteByUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(usuarioHistorico)
            .from(usuarioHistorico)
            .innerJoin(usuarioHistorico.motivoInativacao, motivoInativacao).fetchJoin()
            .innerJoin(usuarioHistorico.usuario, usuario).fetchJoin()
            .where(usuario.id.eq(usuarioId))
            .fetch();
    }

    @Override
    public String findMotivoInativacao(Integer usuarioId) {
        // TODO concertar a Query
        return new JPAQueryFactory(entityManager)
            .select(motivoInativacao.descricao)
            .from(usuarioHistorico)
            .leftJoin(usuario)
            .on(usuarioHistorico.usuario.id.eq(usuario.id))
            .leftJoin(usuarioHistorico.motivoInativacao, motivoInativacao)
            .on(usuarioHistorico.motivoInativacao.id.eq(motivoInativacao.id))
            .where(usuario.id.eq(usuarioId))
            .where(usuarioHistorico.situacao.eq(ESituacao.I))
            .where(motivoInativacao.descricao.isNotEmpty())
            .orderBy(usuarioHistorico.dataCadastro.desc())
            .fetch()
            .get(0);
    }
}
