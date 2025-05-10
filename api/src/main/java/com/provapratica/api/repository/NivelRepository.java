package com.provapratica.api.repository;

import com.provapratica.api.domain.Estudante;
import com.provapratica.api.domain.Nivel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NivelRepository extends PagingAndSortingRepository<Nivel, Integer>,
        CrudRepository<Nivel, Integer> {

}