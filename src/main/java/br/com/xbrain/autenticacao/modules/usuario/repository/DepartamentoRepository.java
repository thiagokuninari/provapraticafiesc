package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DepartamentoRepository extends PagingAndSortingRepository<Cargo, Integer>,
        DepartamentoRepositoryCustom{
}
