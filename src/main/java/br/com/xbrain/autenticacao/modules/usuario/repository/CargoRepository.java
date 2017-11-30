package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CargoRepository extends PagingAndSortingRepository<Cargo, Integer>, CargoRepositoryCustom {

    Cargo findByCodigo(CodigoCargo codigo);

    Optional<Cargo> findByNomeAndNivelId(String nome, int nivelId);
}

