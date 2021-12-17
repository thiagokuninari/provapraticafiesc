package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

@SuppressWarnings("PMD.TooManyStaticImports")
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
    public List<UsuarioCidadeDto> findCidadesDtoByUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(
                Projections.constructor(UsuarioCidadeDto.class,
                    cidade.id, cidade.nome,
                    uf1.id, uf1.nome,
                    regional.id, regional.nome))
            .from(usuarioCidade)
            .join(usuarioCidade.cidade, cidade)
            .leftJoin(cidade.uf, uf1)
            .leftJoin(cidade.regional, regional)
            .where(usuarioCidade.usuario.id.eq(usuarioId))
            .fetch();
    }
}
