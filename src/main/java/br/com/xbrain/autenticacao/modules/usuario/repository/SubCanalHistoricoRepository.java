package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.SubCanalHistorico;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubCanalHistoricoRepository extends CrudRepository<SubCanalHistorico, Integer> {

    List<SubCanalHistorico> findBySubCanal_Id(Integer id);

}
