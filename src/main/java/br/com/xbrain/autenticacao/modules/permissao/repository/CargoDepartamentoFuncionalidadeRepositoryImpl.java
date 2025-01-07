package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.infra.JoinDescriptor;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidadeCanal.funcionalidadeCanal;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;
import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;
import static java.util.Arrays.asList;

@SuppressWarnings("PMD.TooManyStaticImports")
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
                .orderBy(funcionalidade.aplicacao.nome.asc(),
                        funcionalidade.nome.asc())
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

    @Override
    public List<Funcionalidade> findPermissoesEspeciaisDoUsuarioComCanal(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(funcionalidade)
                .distinct()
                .from(permissaoEspecial)
                .innerJoin(permissaoEspecial.funcionalidade, funcionalidade)
                .innerJoin(funcionalidade.canais, funcionalidadeCanal).fetchJoin()
                .where(permissaoEspecial.usuario.id.eq(usuarioId)
                        .and(permissaoEspecial.dataBaixa.isNull()))
                .orderBy(funcionalidade.id.asc())
                .fetch();
    }

    @Override
    public List<Funcionalidade> findFuncionalidadesDoCargoDepartamentoComCanal(Integer cargoId,
                                                                               Integer departamentoId) {
        return new JPAQueryFactory(entityManager)
                .select(funcionalidade)
                .distinct()
                .from(cargoDepartamentoFuncionalidade)
                .innerJoin(cargoDepartamentoFuncionalidade.funcionalidade, funcionalidade)
                .innerJoin(funcionalidade.canais, funcionalidadeCanal).fetchJoin()
                .where(cargoDepartamentoFuncionalidade.cargo.id.eq(cargoId)
                        .and(cargoDepartamentoFuncionalidade.departamento.id.eq(departamentoId)))
                .orderBy(funcionalidade.id.asc())
                .fetch();
    }

    @Override
    public List<Departamento> findAllDepartamentos(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(departamento)
            .distinct()
            .from(cargoDepartamentoFuncionalidade)
            .innerJoin(cargoDepartamentoFuncionalidade.departamento, departamento)
            .innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo)
            .where(departamento.nivel.id.eq(cargo.nivel.id)
                .and(predicate))
            .fetch();
    }

    @Override
    public List<Nivel> getNiveisByFuncionalidades(List<Integer> funcionalidadesIds) {
        return new JPAQueryFactory(entityManager)
            .select(cargoDepartamentoFuncionalidade.cargo.nivel)
            .distinct()
            .from(cargoDepartamentoFuncionalidade)
            .join(cargoDepartamentoFuncionalidade.cargo, cargo)
            .join(cargo.nivel, nivel)
            .where(cargoDepartamentoFuncionalidade.funcionalidade.id.in(funcionalidadesIds)
                .and(nivel.situacao.eq(ESituacao.A)))
            .fetch();
    }
}
