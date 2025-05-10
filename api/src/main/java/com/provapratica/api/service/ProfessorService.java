package com.provapratica.api.service;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.domain.Especialidade;
import com.provapratica.api.domain.Professor;
import com.provapratica.api.dto.ProfessorRequest;
import com.provapratica.api.dto.ProfessorResponse;
import com.provapratica.api.repository.EspecialidadeRepository;
import com.provapratica.api.repository.ProfessorRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.provapratica.api.comun.CpfUtil.removerCaracteresDoCpf;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    public List<ProfessorResponse> findAll() {
        return StreamSupport.stream(professorRepository.findAll().spliterator(), false)
                .map(ProfessorResponse::of)
                .collect(Collectors.toList());
    }

    public ProfessorResponse save(ProfessorRequest professorRequest) {
        var especialidade = findEspecialidadeById(professorRequest.getEspecialidadeId());

        validarCpf(professorRequest.getCpf());
        var professor = professorRepository.save(Professor.of(professorRequest, especialidade));
        return ProfessorResponse.of(professor);
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new ValidationException("CPF não pode ser nulo ou vazio");
        }

        var cpfSemMascara = CpfUtil.removerCaracteresDoCpf(cpf);

        if (!CpfUtil.isCpfValido(cpfSemMascara)) {
            throw new ValidationException("CPF inválido");
        }

        validarCpfExistente(cpfSemMascara);
    }

    private void validarCpfExistente(String cpfSemMascara) {
        professorRepository.findByCpf(cpfSemMascara)
                .ifPresent(professor -> {
                    throw new ValidationException("CPF já cadastrado para o professor.");
                });
    }

    @Transactional
    public ProfessorResponse update(Integer id, ProfessorRequest professorRequest) {
        var professor = findById(id);
        var cpfRequest = removerCaracteresDoCpf(professorRequest.getCpf());
        var cpfAtual = removerCaracteresDoCpf(professor.getCpf());

        if (!cpfAtual.equals(cpfRequest)) {
            throw new ValidationException("CPF não pode ser alterado.");
        }

        BeanUtils.copyProperties(professorRequest, professor, "cpf");

        professorRepository.save(professor);
        return ProfessorResponse.of(professor);
    }


    public Professor findById(Integer id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Professor não encontrado."));
    }

    public Especialidade findEspecialidadeById(Integer id) {
        return especialidadeRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Especialidade não encontrada."));
    }
}
