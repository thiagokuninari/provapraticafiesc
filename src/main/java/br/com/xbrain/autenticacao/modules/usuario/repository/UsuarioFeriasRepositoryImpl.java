package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioFerias.usuarioFerias;
import static com.querydsl.jpa.JPAExpressions.select;

public class UsuarioFeriasRepositoryImpl extends CustomRepository<UsuarioFerias> implements UsuarioFeriasRepositoryCustom {

    @Override
    public List<Usuario> getUsuariosInativosComFeriasEmAberto(LocalDate dataFimFerias) {
        return new JPAQueryFactory(entityManager)
                .select(usuario)
                .from(usuario)
                .where(usuario.id.in(select(usuarioFerias.usuario.id)
                        .from(usuarioFerias)
                        .where(usuarioFerias.fim.eq(dataFimFerias))))
                .fetch();
    }
}
