package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.infra.JoinDescriptor;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static java.util.Arrays.asList;

public class CargoDepartamentoFuncionalidadeRepositoryImpl
        extends CustomRepository<CargoDepartamentoFuncionalidade>
        implements CargoDepartamentoFuncionalidadeRepositoryCustom {

    @Override
    public List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cargoDepartamentoFuncionalidade)
                .from(cargoDepartamentoFuncionalidade)
                .innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo).fetchJoin()
                .innerJoin(cargo.nivel).fetchJoin()
                .innerJoin(cargoDepartamentoFuncionalidade.departamento).fetchJoin()
                .innerJoin(cargoDepartamentoFuncionalidade.funcionalidade, funcionalidade).fetchJoin()
                .innerJoin(funcionalidade.aplicacao).fetchJoin()
                .where(predicate)
                .orderBy(funcionalidade.nome.asc())
                .fetch();
    }

    @Override
    public Page<CargoDepartamentoFuncionalidade> findAll(Predicate predicate, Pageable pageable) {
        return super.findAll(
                asList(
                        JoinDescriptor.innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo),
                        JoinDescriptor.innerJoin(cargoDepartamentoFuncionalidade.departamento),
                        JoinDescriptor.innerJoin(cargoDepartamentoFuncionalidade.funcionalidade, funcionalidade)
                ),
                predicate,
                pageable,
                new Sort(Sort.Direction.ASC, funcionalidade.nome.toString()));
    }
}
