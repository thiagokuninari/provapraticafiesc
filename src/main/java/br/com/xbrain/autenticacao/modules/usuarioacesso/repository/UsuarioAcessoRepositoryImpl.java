package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso.usuarioAcesso;
import static com.querydsl.core.types.Projections.constructor;

public class UsuarioAcessoRepositoryImpl
        extends CustomRepository<UsuarioHierarquia> implements UsuarioAcessoRepositoryCustom {

    private static final int DOIS_MESES = 2;

    @Override
    public List<UsuarioAcesso> findAllUltimoAcessoUsuarios() {
        return new JPAQueryFactory(entityManager)
            .select(constructor(
                UsuarioAcesso.class, usuarioAcesso.dataCadastro.max(),
                usuarioAcesso.usuario.id, usuarioAcesso.usuario.email))
            .from(usuarioAcesso)
            .innerJoin(usuarioAcesso.usuario, usuario)
            .where(usuario.situacao.eq(ESituacao.A))
            .groupBy(usuarioAcesso.usuario.id, usuarioAcesso.usuario.email)
            .fetch();
    }

    @Override
    public long deletarHistoricoUsuarioAcesso() {
        return new JPAQueryFactory(entityManager)
                .delete(usuarioAcesso)
                .where(usuarioAcesso.dataCadastro.before(
                        LocalDateTime.now().minusMonths(DOIS_MESES)))
                .execute();
    }

    @Override
    public long countUsuarioAcesso() {
        return new JPAQueryFactory(entityManager)
                .select(usuarioAcesso)
                .from(usuarioAcesso)
                .fetchCount();
    }
}
