package com.provapratica.api.service;

import com.provapratica.api.domain.Agendamento;
import com.provapratica.api.dto.AgendamentoRequest;
import com.provapratica.api.dto.AgendamentoResponse;
import com.provapratica.api.repository.AgendamentoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public AgendamentoResponse save(AgendamentoRequest agendamentoRequest) {
        return AgendamentoResponse.of(agendamentoRepository.save(Agendamento.of(agendamentoRequest)));
    }

    @Transactional
    public AgendamentoResponse update(Integer id, AgendamentoRequest agendamentoRequest) {
        var agendamento = findById(id);

        BeanUtils.copyProperties(agendamentoRequest, agendamento);

        agendamentoRepository.save(agendamento);
        return AgendamentoResponse.of(agendamento);
    }

    public Agendamento findById(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Agendamento não encontrado."));
    }
}
