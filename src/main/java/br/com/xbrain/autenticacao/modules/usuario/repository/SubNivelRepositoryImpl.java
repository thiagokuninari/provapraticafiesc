package br.com.xbrain.autenticacao.modules.usuario.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargoFuncionalidadeSubNivel.cargoFuncionalidadeSubNivel;
import static br.com.xbrain.autenticacao.modules.usuario.model.QSubNivel.subNivel;

@RequiredArgsConstructor
public class SubNivelRepositoryImpl implements SubNivelRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Integer> findFuncionalidadesIdsByNivelId(Integer nivelId) {
        return new JPAQueryFactory(entityManager)
            .select(cargoFuncionalidadeSubNivel.funcionalidade.id)
            .from(subNivel)
            .innerJoin(subNivel.cargoFuncionalidadeSubNiveis, cargoFuncionalidadeSubNivel)
            .innerJoin(cargoFuncionalidadeSubNivel.funcionalidade, funcionalidade)
            .where(subNivel.nivel.id.eq(nivelId))
            .fetch();
    }
}
