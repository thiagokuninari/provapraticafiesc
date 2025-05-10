package com.provapratica.api.repository;

import com.provapratica.api.domain.Estudante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EstudanteRepository extends PagingAndSortingRepository<Estudante, Integer>,
        CrudRepository<Estudante, Integer>{

    Optional<Estudante> findTop1EstudanteByCpf(String cpf);

}
