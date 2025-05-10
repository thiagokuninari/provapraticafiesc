package com.provapratica.api.service;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.domain.Estudante;
import com.provapratica.api.dto.EstudanteRequest;
import com.provapratica.api.dto.EstudanteResponse;
import com.provapratica.api.repository.EstudanteRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.provapratica.api.comun.CpfUtil.removerCaracteresDoCpf;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class EstudanteService {

    @Autowired
    private EstudanteRepository estudanteRepository;


    public List<EstudanteResponse> findAll() {
        return StreamSupport.stream(estudanteRepository.findAll().spliterator(), false)
                .map(estudante -> new EstudanteResponse(
                        estudante.getNome(), estudante.getWhatsapp()))
                .collect(Collectors.toList());
    }

    public EstudanteResponse save(EstudanteRequest request) {
        validarCpf(request.getCpf());
        var estudante = estudanteRepository.save(Estudante.of(request));
        return EstudanteResponse.of(estudante);
    }

    private void validarCpf(String cpf) {
        validarCpfExistente(cpf);
        CpfUtil.isCpfValido(cpf);
    }

    private void validarCpfExistente(String cpf) {
        if (cpf == null) {
            return;
        }
        var cpfSemMascara = removerCaracteresDoCpf(cpf);

        estudanteRepository.findTop1EstudanteByCpf(cpfSemMascara)
                .ifPresent(estudanteExistente -> {
                    if (isEmpty(estudanteExistente.getId())) {
                        throw new ValidationException("CPF já cadastrado.");
                    }
                });
    }

    @Transactional
    public EstudanteResponse update(Integer id, EstudanteRequest estudanteRequest) {
        var estudante = findById(id);
        var cpfRequest = removerCaracteresDoCpf(estudanteRequest.getCpf());
        var cpfAtual = removerCaracteresDoCpf(estudante.getCpf());

        if (!cpfAtual.equals(cpfRequest)) {
            throw new ValidationException("CPF não pode ser alterado.");
        }

        BeanUtils.copyProperties(estudanteRequest, estudante, "cpf");

        estudanteRepository.save(estudante);
        return EstudanteResponse.of(estudante);
    }


    public Estudante findById(Integer id) {
        return estudanteRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Estudante não encontrado."));
    }
}
