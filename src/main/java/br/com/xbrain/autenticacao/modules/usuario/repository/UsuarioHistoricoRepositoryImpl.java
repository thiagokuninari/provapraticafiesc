package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHistorico.usuarioHistorico;

public class UsuarioHistoricoRepositoryImpl
        extends CustomRepository<UsuarioHistorico> implements UsuarioHistoricoRepositoryCustom {

    @Override
    public Optional<UsuarioHistorico> getUltimoHistoricoPorUsuario(Integer usuarioId) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuarioHistorico)
                        .from(usuarioHistorico)
                        .where(usuarioHistorico.usuario.id.eq(usuarioId)
                                .and(usuarioHistorico.motivoInativacao
                                        .codigo.eq(CodigoMotivoInativacao.ULTIMO_ACESSO)))
                        .fetchOne());
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

    @Override
    public List<Usuario> getUsuariosSemAcesso() {
        List<BigDecimal> usuariosHistorico = entityManager.createNativeQuery(
                "SELECT distinct U.ID "
                        + "  FROM USUARIO_HISTORICO UH "
                        + " INNER JOIN USUARIO U ON U.ID = UH.FK_USUARIO"
                        + " WHERE UH.FK_MOTIVO_INATIV = 5 "
                        + " AND U.SITUACAO = 'A' "
                        + " AND (SYSDATE - TRUNC(UH.DATA_CADASTRO)) >= 32")
                .getResultList();
        return usuariosHistorico.stream()
                .map(h -> {
                    return new Usuario(h.intValue());
                })
                .collect(Collectors.toList());
    }

}
