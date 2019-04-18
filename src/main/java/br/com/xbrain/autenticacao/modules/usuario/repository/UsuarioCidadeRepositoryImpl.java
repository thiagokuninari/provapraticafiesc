package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class UsuarioCidadeRepositoryImpl extends CustomRepository<UsuarioCidade> implements UsuarioCidadeRepositoryCustom {

    @Override
    public List<Integer> findCidadesIdByUsuarioId(int usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(usuarioCidade.cidade.id)
                .from(usuarioCidade)
                .where(usuarioCidade.usuario.id.eq(usuarioId))
                .fetch();
    }
}
