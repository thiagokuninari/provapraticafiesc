package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioAfastamento;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioAfastamento.usuarioAfastamento;
import static com.querydsl.jpa.JPAExpressions.select;

public class UsuarioAfastamentoRepositoryImpl extends CustomRepository<UsuarioAfastamento>
    implements UsuarioAfastamentoRepositoryCustom {

    @Override
    public List<Usuario> getUsuariosInativosComAfastamentoEmAberto(LocalDate dataFimAfastamento) {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .where(usuario.id.in(select(usuarioAfastamento.usuario.id)
                .from(usuarioAfastamento)
                .where(usuarioAfastamento.fim.eq(dataFimAfastamento))))
            .fetch();
    }

    @Override
    public Long atualizaDataFim(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .update(usuarioAfastamento)
            .where(usuarioAfastamento.usuario.id.eq(usuarioId)
                .and(usuarioAfastamento.fim.isNull()))
            .set(usuarioAfastamento.fim, LocalDate.now())
            .execute();
    }
}