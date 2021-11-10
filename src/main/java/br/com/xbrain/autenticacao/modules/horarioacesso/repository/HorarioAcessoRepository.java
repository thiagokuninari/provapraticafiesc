package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;

public interface HorarioAcessoRepository extends 
        PagingAndSortingRepository<HorarioAcesso, Integer>,
        HorarioAcessoRepositoryCustom {

}
