package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DepartamentoRepository extends PagingAndSortingRepository<Departamento, Integer>,
        DepartamentoRepositoryCustom {

    Departamento findByCodigo(CodigoDepartamento codigo);

}
