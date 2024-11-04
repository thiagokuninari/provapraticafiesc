package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CargoRepository extends PagingAndSortingRepository<Cargo, Integer>, CargoRepositoryCustom {

    Cargo findByCodigo(CodigoCargo codigo);

    List<Cargo> findByCodigoIn(List<CodigoCargo> codigoCargo);

    Optional<Cargo> findFirstByNomeIgnoreCaseAndNivelId(String nome, int nivelId);

    Optional<Cargo> findById(Integer id);

    boolean existsByCodigoAndSituacao(CodigoCargo codigo, ESituacao situacao);

    boolean existsByCodigoAndSituacaoAndIdNot(CodigoCargo codigo, ESituacao situacao, Integer id);
}

