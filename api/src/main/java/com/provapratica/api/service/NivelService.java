package com.provapratica.api.service;

import com.provapratica.api.domain.Nivel;
import com.provapratica.api.dto.EstudanteResponse;
import com.provapratica.api.dto.NivelResponse;
import com.provapratica.api.repository.EstudanteRepository;
import com.provapratica.api.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;

    public List<NivelResponse> findAll() {
        return StreamSupport.stream(nivelRepository.findAll().spliterator(), false)
                .map(nivel -> new NivelResponse(
                        nivel.getId(),
                        nivel.getNome(),
                        nivel.getCodigo().name()))
                .collect(Collectors.toList());
    }
}
