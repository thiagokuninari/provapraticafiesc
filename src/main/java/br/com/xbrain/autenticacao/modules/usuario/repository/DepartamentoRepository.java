package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DepartamentoRepository extends PagingAndSortingRepository<Departamento, Integer>,
        DepartamentoRepositoryCustom {

    Departamento findByCodigo(CodigoDepartamento codigo);

    Optional<Departamento> findByCodigoAndNivelId(CodigoDepartamento codigo, Integer id);

}
