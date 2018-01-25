package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;

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
                .distinct()
                .fetch()
                .stream()
                .map(f -> {
                    f.setEspecial(true);
                    return f;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> findUsuarioComPermissaoEspecial(CodigoFuncionalidade codigoFuncionalidade) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(permissaoEspecial.usuario)
                        .from(permissaoEspecial)
                        .innerJoin(permissaoEspecial.funcionalidade, funcionalidade)
                        .where(funcionalidade.role.eq(codigoFuncionalidade.name())
                                .and(permissaoEspecial.dataBaixa.isNull()))
                        .limit(1)
                        .fetchFirst());
    }
}
