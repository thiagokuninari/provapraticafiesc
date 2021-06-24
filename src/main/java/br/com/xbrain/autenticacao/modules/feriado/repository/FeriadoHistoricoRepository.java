package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoHistorico;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeriadoHistoricoRepository extends PagingAndSortingRepository<FeriadoHistorico, Integer> {
}
