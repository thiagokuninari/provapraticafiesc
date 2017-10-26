package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;

public class CargoDepartamentoFuncionalidadeRepositoryImpl implements CargoDepartamentoFuncionalidadeRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    public List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(
            Cargo cargoObj, Departamento departamento) {
        return new JPAQueryFactory(entityManager)
                .select(cargoDepartamentoFuncionalidade)
                .from(cargoDepartamentoFuncionalidade)
                .innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo).fetchJoin()
                .innerJoin(cargo.nivel).fetchJoin()
                .innerJoin(cargoDepartamentoFuncionalidade.departamento).fetchJoin()
                .leftJoin(cargoDepartamentoFuncionalidade.funcionalidade).fetchJoin()
                .where(cargoDepartamentoFuncionalidade.cargo.id.eq(cargoObj.getId())
                        .and(cargoDepartamentoFuncionalidade.departamento.id.eq(departamento.getId())))
                .fetch();
    }
}
