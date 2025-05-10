package com.provapratica.api.service;

import com.provapratica.api.domain.Especialidade;
import com.provapratica.api.dto.EspecialidadeRequest;
import com.provapratica.api.dto.EspecialidadeResponse;
import com.provapratica.api.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    public EspecialidadeResponse save(EspecialidadeRequest request) {
        return EspecialidadeResponse.of(especialidadeRepository.save(Especialidade.of(request)));
    }
}
