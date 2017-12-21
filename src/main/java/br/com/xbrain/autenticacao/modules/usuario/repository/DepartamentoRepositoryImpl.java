package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static br.com.xbrain.autenticacao.infra.JoinDescriptor.innerJoin;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;
import static java.util.Arrays.asList;

public class DepartamentoRepositoryImpl  extends CustomRepository<Departamento>
        implements DepartamentoRepositoryCustom {

    public Page<Departamento> findAll(Predicate predicate, Pageable pageable) {
        return super.findAll(
                asList(
                        innerJoin(departamento.nivel)
                ),
                predicate,
                pageable);
    }

    @Override
    public Iterable<Departamento> findBySituacaoAndNivelId(ESituacao situacao, Integer nivelId) {
        return new JPAQueryFactory(entityManager)
                .select(departamento)
                .from(departamento)
                .innerJoin(departamento.nivel).fetchJoin()
                .where(
                        departamento.nivel.id.eq(nivelId)
                                .and(departamento.situacao.eq(situacao))
                )
                .orderBy(departamento.nome.asc())
                .fetch();
    }

}


