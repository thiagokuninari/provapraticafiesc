package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.SubCanalHistorico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubCanalHistoricoRepository extends PagingAndSortingRepository<SubCanalHistorico, Integer> {

    Page<SubCanalHistorico> findBySubCanal_Id(Integer id, Pageable pageable);

}
