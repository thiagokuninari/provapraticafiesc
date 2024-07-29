package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
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

    @Override
    public List<Integer> findCidadesIdByUsuarioIdComDataBaixaNull (Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(usuarioCidade.cidade.id)
            .from(usuarioCidade)
            .where(usuarioCidade.dataBaixa.isNull().and(usuarioCidade.usuario.id.eq(usuarioId)))
            .fetch();
    }

    @Override
    public Set<UsuarioCidade> findUsuarioCidadesByUsuarioId(Integer usuarioId) {
        return new HashSet<>(new JPAQueryFactory(entityManager)
            .selectFrom(usuarioCidade)
            .join(usuarioCidade.cidade, cidade)
            .leftJoin(cidade.uf, uf1)
            .leftJoin(cidade.regional, regional)
            .where(usuarioCidade.usuario.id.eq(usuarioId))
            .fetch());
    }
}
