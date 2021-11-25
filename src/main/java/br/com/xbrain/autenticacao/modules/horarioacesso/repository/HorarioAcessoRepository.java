package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import org.springframework.data.repository.CrudRepository;

public interface HorarioAcessoRepository extends 
    CrudRepository<HorarioAcesso, Integer>, HorarioAcessoRepositoryCustom {

}
