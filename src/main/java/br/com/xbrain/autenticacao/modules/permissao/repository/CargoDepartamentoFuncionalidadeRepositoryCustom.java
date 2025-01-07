package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CargoDepartamentoFuncionalidadeRepositoryCustom {

    List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(Predicate predicate);

    Page<CargoDepartamentoFuncionalidade> findAll(Predicate predicate, Pageable pageable);

    List<Funcionalidade> findPermissoesEspeciaisDoUsuarioComCanal(Integer usuarioId);

    List<Funcionalidade> findFuncionalidadesDoCargoDepartamentoComCanal(Integer cargoId,
                                                                        Integer departamentoId);

    List<Departamento> findAllDepartamentos(Predicate predicate);

    List<Nivel> getNiveisByFuncionalidades(List<Integer> funcionalidadesIds);
}
