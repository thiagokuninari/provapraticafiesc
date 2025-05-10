package com.provapratica.api.repository;

import com.provapratica.api.domain.Especialidade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EspecialidadeRepository extends PagingAndSortingRepository<Especialidade, Integer>,
        CrudRepository<Especialidade, Integer> {
}
