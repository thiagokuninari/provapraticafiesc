package com.provapratica.api.repository;

import com.provapratica.api.domain.Agendamento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AgendamentoRepository extends PagingAndSortingRepository<Agendamento, Integer>,
        CrudRepository<Agendamento, Integer> {
}
