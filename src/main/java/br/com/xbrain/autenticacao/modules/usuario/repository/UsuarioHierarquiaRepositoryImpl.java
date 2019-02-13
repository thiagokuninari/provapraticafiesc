package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;

public class UsuarioHierarquiaRepositoryImpl
        extends CustomRepository<UsuarioHierarquia>
        implements UsuarioHierarquiaRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    public List<UsuarioHierarquia> findAllByIdUsuarioSuperior(Integer idUsuario) {
        return new JPAQueryFactory(entityManager)
                .select(usuarioHierarquia)
                .from(usuarioHierarquia)
                .join(usuarioHierarquia.usuario, usuario).fetchJoin()
                .join(usuario.cargo, cargo).fetchJoin()
                .join(cargo.cargoSuperior).fetchJoin()
                .where(usuarioHierarquia.usuarioSuperior.id.eq(idUsuario))
                .fetch();
    }
}
