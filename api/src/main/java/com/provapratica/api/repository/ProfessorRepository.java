package com.provapratica.api.repository;

import com.provapratica.api.domain.Professor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProfessorRepository extends PagingAndSortingRepository<Professor, Integer>
        , CrudRepository<Professor, Integer> {

    Optional<Professor> findByCpf(String cpf);

}
