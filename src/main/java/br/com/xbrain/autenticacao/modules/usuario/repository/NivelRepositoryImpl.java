package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;

public class NivelRepositoryImpl extends CustomRepository<Nivel> implements NivelRepositoryCustom {

    @Override
    public List<Nivel> getAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(nivel)
                .from(nivel)
                .where(predicate)
                .orderBy(nivel.nome.asc())
                .fetch();
    }

    @Override
    public List<Nivel> getNiveisConfiguracoesTratativas(List<Integer> funcionalidadesIds) {
        return new JPAQueryFactory(entityManager)
            .select(cargoDepartamentoFuncionalidade.cargo.nivel)
            .from(cargoDepartamentoFuncionalidade)
            .join(cargoDepartamentoFuncionalidade.cargo, cargo)
            .join(cargo.nivel, nivel)
            .where(cargoDepartamentoFuncionalidade.funcionalidade.id.in(funcionalidadesIds)
                .and(nivel.situacao.eq(ESituacao.A)))
            .distinct()
            .fetch();
    }
}
