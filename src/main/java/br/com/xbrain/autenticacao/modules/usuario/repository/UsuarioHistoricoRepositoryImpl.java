package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.model.QMotivoInativacao.*;
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
    public List<UsuarioHistoricoDto> getHistoricoDoUsuario(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(UsuarioHistoricoDto.class, usuarioHistorico))
                .from(usuarioHistorico)
                .where(usuarioHistorico.usuario.id.eq(usuarioId)
                        .and(usuarioHistorico.motivoInativacao.isNotNull()))
                .orderBy(usuarioHistorico.dataCadastro.desc())
                .fetch();
    }

    public List<Usuario> getUsuariosPorTempoDeInatividade(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .selectDistinct(usuarioHistorico.usuario)
                .from(usuarioHistorico)
                .innerJoin(usuarioHistorico.usuario, usuario)
                .where(usuarioHistorico.motivoInativacao.codigo.eq(CodigoMotivoInativacao.ULTIMO_ACESSO)
                        .and(usuario.situacao.eq(ESituacao.A))
                        .and(predicate))
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
}
