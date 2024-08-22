package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;

public class DepartamentoRepositoryImpl extends CustomRepository<Departamento>
        implements DepartamentoRepositoryCustom {

    @Override
    public List<Departamento> findAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(departamento)
                .from(departamento)
                .where(departamento.situacao.eq(ESituacao.A)
                        .and(predicate))
                .orderBy(departamento.nome.asc())
                .fetch();
    }
}


